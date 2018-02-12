package farijo.com.starcraft_bo_shower.network;

import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import farijo.com.starcraft_bo_shower.file_explorer.BOExplorerActivity;
import farijo.com.starcraft_bo_shower.file_explorer.BOFileAdapter;
import farijo.com.starcraft_bo_shower.file_explorer.VirtualFile;

/**
 * Created by Teddy on 11/02/2018.
 */

public class FileSynchronizer extends Thread {

    private static final String ROOT_NAME = "public";
    private static final long MIN_DOWNLOAD_TIME_MILLIS = 800;

    public static class BasicRequest {
        final VirtualFile representingFile;
        final String fullPath;
        final Short adapterKey;

        public BasicRequest(VirtualFile rf, String fp, Short ak) {
            representingFile = rf;
            fullPath = fp;
            adapterKey = ak;
        }
    }

    public HashMap<Short, BOFileAdapter> activeAdapters = new HashMap<>();
    public final VirtualFile fileSystem;

    private final ConcurrentLinkedQueue<BasicRequest> filesToDownload = new ConcurrentLinkedQueue<>();
    private final Socket socket = new Socket();
    private final BOStream boStream = new BOStream(socket);
    private final BOExplorerActivity wrappingActivity;
    private final String ip;
    private final int port;
    private String toStart = null;

    public FileSynchronizer(BOExplorerActivity wrappingActivity, String ip, int port) {
        this.wrappingActivity = wrappingActivity;
        this.ip = ip;
        this.port = port;

        fileSystem = VirtualFile.loadVirtualFilesLocals(wrappingActivity);
    }

    public void addFileToDownload(BasicRequest request) {
        addFileToDownload(request, false);
    }

    public void addFileToDownload(BasicRequest request, boolean requestStart) {
        synchronized (filesToDownload) {
            filesToDownload.add(request);
            if (requestStart) {
                toStart = request.fullPath;
            }
            filesToDownload.notify();
        }
    }

    public void cancelLaunchRequest() {
        toStart = null;
    }

    @Override
    public void run() {
        try {
            initialiseSocket();
            synchronizeClientWithServerFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addRootFragmentToActivity();

        if(socket.isConnected()) {
            final File localRootFolder = new File(wrappingActivity.getFilesDir(), "files");

            try {
                downloadFileLoop(localRootFolder);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialiseSocket() throws IOException {
        socket.connect(new InetSocketAddress(ip, port), 6000);
    }

    private void synchronizeClientWithServerFiles() throws IOException {
        List<String> files = new ArrayList<>();
        List<Long> updateTimes = new ArrayList<>();

        receiveFileLists(files, updateTimes);

        fileSystem.loadVirtualFilesFromStrings(
                files.toArray(new String[files.size()]),
                updateTimes.toArray(new Long[updateTimes.size()])
        );
    }

    private void receiveFileLists(List<String> files, List<Long> updateTimes) throws IOException {
        String data;
        BOInputStream inputStream = boStream.getBufferedInputStream();
        do {
            data = inputStream.readBOFilename();
            if (data != null) {
                files.add(data);
                final long date = inputStream.readDate();
                updateTimes.add(date);
            }
        } while (data != null);
    }

    private void addRootFragmentToActivity() {
        if (!wrappingActivity.isDestroyed()) {
            wrappingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wrappingActivity.addFragment(ROOT_NAME);
                }
            });
        }
    }

    private void downloadFileLoop(File clientInternalRoot) throws InterruptedException, IOException {
        waitFileToDownload();

        if(wrappingActivity.isDestroyed()) {
            return;
        }

        requestAndReceiveFile(clientInternalRoot);

        downloadFileLoop(clientInternalRoot);
    }

    private void waitFileToDownload() throws InterruptedException {
        synchronized (filesToDownload) {
            while (filesToDownload.isEmpty() && !wrappingActivity.isDestroyed()) {
                filesToDownload.wait();
            }
        }
    }

    private void requestAndReceiveFile(File clientInternalRoot) throws IOException {
        final BasicRequest request = getNextFileRequested();
        final String requestedFilepath = request.fullPath;

        requestFileToServer(requestedFilepath);
        updateDownloadState(DownloadingState.STARTING, request);

        final File receivedFile = receiveAndStoreFileFromServer(clientInternalRoot, requestedFilepath);
        if(receivedFile != null) {
            updateDownloadState(DownloadingState.ENDING_OK, request);
            if (mustBeStarted(requestedFilepath)) {
                cancelLaunchRequest();
                wrappingActivity.startBO(receivedFile.getAbsolutePath());
            }
        } else {
            updateDownloadState(DownloadingState.ENDING_EMPTY, request);
            if (mustBeStarted(requestedFilepath)) {
                cancelLaunchRequest();
            }
        }
    }

    private BasicRequest getNextFileRequested() {
        return filesToDownload.poll();
    }

    private void requestFileToServer(String filepath) throws IOException {
        boStream.getOutputStream().writeFilename(filepath);
    }

    private File receiveAndStoreFileFromServer(File storageRoot, String storePath) throws IOException {
        BOInputStream inputStream = boStream.getInputStream();
        byte[] fileBytes = inputStream.readBOFile();
        if (fileBytes != null) {
            final File file = new File(storageRoot, storePath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileBytes);
            fileOutputStream.close();
            file.setLastModified(inputStream.readDate());
            return file;
        } else {
            return null;
        }
    }

    private boolean mustBeStarted(String path) {
        return toStart.equals(path);
    }

    private enum DownloadingState{ STARTING, ENDING_OK, ENDING_EMPTY }
    private void updateDownloadState(final DownloadingState state, final BasicRequest request) {
        final short adapterId = request.adapterKey;
        final VirtualFile virtualFile = request.representingFile;
        wrappingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case STARTING:
                        virtualFile.startDownload();
                        break;
                    case ENDING_OK:
                        virtualFile.endDownload();
                        Toast.makeText(wrappingActivity, virtualFile.fileName + " downloaded", Toast.LENGTH_SHORT).show();
                        break;
                    case ENDING_EMPTY:
                        virtualFile.endDownloadEmpty();
                        Toast.makeText(wrappingActivity, virtualFile.fileName + " is empty", Toast.LENGTH_SHORT).show();
                        break;
                }
                if(activeAdapters.containsKey(adapterId)) {
                    activeAdapters.get(adapterId).notifyChanged(virtualFile);
                }
            }
        });
    }
}
