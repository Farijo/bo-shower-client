package farijo.com.starcraft_bo_shower.network.custom_server;

import android.os.SystemClock;
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
    private static final long MIN_DOWNLOAD_TIME_MILLIS = 650;

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

    private final VirtualFile fileSystem;
    private final ConcurrentLinkedQueue<BasicRequest> filesToDownload = new ConcurrentLinkedQueue<>();
    private final Socket socket = new Socket();
    private final BOStream boStream = new BOStream(socket);
    private final BOExplorerActivity wrappingActivity;
    private final String ip;
    private final int port;
    private String toStart = null;
    private long downloadStartTime = 0L;

    public FileSynchronizer(BOExplorerActivity wrappingActivity, String ip, int port) {
        this.wrappingActivity = wrappingActivity;
        this.ip = ip;
        this.port = port;

        fileSystem = VirtualFile.loadVirtualFilesLocals(wrappingActivity);
    }

    public VirtualFile getFileSystem() {
        return fileSystem;
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

    public void resetLaunchRequest() {
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

        requestFileToServer(request);

        final File receivedFile = receiveAndStoreFileFromServer(clientInternalRoot, request);

        if (mustBeStarted(requestedFilepath)) {
            resetLaunchRequest();
            checkAndLaunch(receivedFile);
        }
    }

    private BasicRequest getNextFileRequested() {
        return filesToDownload.poll();
    }

    private void requestFileToServer(BasicRequest request) throws IOException {
        boStream.getOutputStream().writeFilename(request.fullPath);

        updateDownloadState(DownloadingState.STARTING, request);
    }

    private File receiveAndStoreFileFromServer(File storageRoot, BasicRequest request) throws IOException {
        BOInputStream inputStream = boStream.getInputStream();
        byte[] fileBytes = inputStream.readBOFile();
        if (fileBytes != null) {
            final File file = new File(storageRoot, request.fullPath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileBytes);
            fileOutputStream.close();
            file.setLastModified(inputStream.readDate());
            updateDownloadState(DownloadingState.ENDING_OK, request);
            return file;
        } else {
            updateDownloadState(DownloadingState.ENDING_EMPTY, request);
            return null;
        }
    }

    private boolean mustBeStarted(String path) {
        return path.equals(toStart);
    }

    private void checkAndLaunch(File boFile) {
        if(boFile != null) {
            wrappingActivity.startBO(boFile.getAbsolutePath());
        }
    }

    private enum DownloadingState{ STARTING, ENDING_OK, ENDING_EMPTY }
    private void updateDownloadState(final DownloadingState state, final BasicRequest request) {
        final short adapterId = request.adapterKey;
        final VirtualFile virtualFile = request.representingFile;

        switch (state) {
            case STARTING:
                downloadStartTime = SystemClock.currentThreadTimeMillis();
                virtualFile.startDownload();
                break;
            case ENDING_OK:
                sleepUntilMinDownloadTime();
                virtualFile.endDownload();
                showToast(state);
                break;
            case ENDING_EMPTY:
                sleepUntilMinDownloadTime();
                virtualFile.endDownloadEmpty();
                showToast(state);
                break;
        }

        notifyVirtualFileChanged(adapterId, virtualFile);
    }

    private void sleepUntilMinDownloadTime() {
        final long downloadDeltaTime = MIN_DOWNLOAD_TIME_MILLIS - (SystemClock.currentThreadTimeMillis() - downloadStartTime);
        if(0 < downloadDeltaTime) {
            try {
                sleep(downloadDeltaTime);
            } catch (InterruptedException ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private void showToast(final DownloadingState state) {
        wrappingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(wrappingActivity, "Téléchargement terminé : "+state, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyVirtualFileChanged(final Short adapterId, final VirtualFile virtualFile) {
        wrappingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(activeAdapters.containsKey(adapterId)) {
                    activeAdapters.get(adapterId).notifyChanged(virtualFile);
                }
            }
        });
    }
}
