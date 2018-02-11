package farijo.com.starcraft_bo_shower.network;

import android.content.Intent;
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
import farijo.com.starcraft_bo_shower.player.BOActivity;

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

    public String toStart = null;
    public HashMap<Short, BOFileAdapter> activeAdapters = new HashMap<>();
    public final VirtualFile fileSystem;

    private final ConcurrentLinkedQueue<BasicRequest> filesToDownload = new ConcurrentLinkedQueue<>();
    private final Socket socket = new Socket();
    private final BOStream boStream = new BOStream(socket);
    private final BOExplorerActivity wrappingActivity;
    private final String ip;
    private final int port;

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

        final BasicRequest fileData = filesToDownload.poll();

        updateDownloadState(DownloadingState.STARTING, fileData.adapterKey, fileData.representingFile);
        final long startTime = SystemClock.currentThreadTimeMillis();
        final String path = fileData.fullPath;
        boStream.getOutputStream().writeFilename(path);
        BOInputStream inputStream = boStream.getInputStream();
        byte[] filebytes = inputStream.readBOFile();

        if (filebytes != null) {
            final File file = new File(clientInternalRoot, path);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(filebytes);
            fileOutputStream.close();
            file.setLastModified(inputStream.readDate());
            final long restingTime = MIN_DOWNLOAD_TIME_MILLIS - (SystemClock.currentThreadTimeMillis() - startTime);
            if (restingTime > 0) {
                sleep(restingTime);
            }
            updateDownloadState(DownloadingState.ENDING_OK, fileData.adapterKey, fileData.representingFile);
            if (path.equals(toStart)) {
                toStart = null;
                Intent intent = new Intent(wrappingActivity, BOActivity.class);
                intent.putExtra(BOActivity.BO_EXTRA, file.getAbsolutePath());
                wrappingActivity.startActivity(intent);
            }
        } else {
            if (path.equals(toStart)) {
                toStart = null;
            }
            updateDownloadState(DownloadingState.ENDING_EMPTY, fileData.adapterKey, fileData.representingFile);
        }

        downloadFileLoop(clientInternalRoot);
    }

    private void waitFileToDownload() throws InterruptedException {
        synchronized (filesToDownload) {
            while (filesToDownload.isEmpty() && !wrappingActivity.isDestroyed()) {
                filesToDownload.wait();
            }
        }
    }

    private enum DownloadingState{ STARTING, ENDING_OK, ENDING_EMPTY }
    private void updateDownloadState(final DownloadingState state, final short adapterId, final VirtualFile virtualFile) {
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
