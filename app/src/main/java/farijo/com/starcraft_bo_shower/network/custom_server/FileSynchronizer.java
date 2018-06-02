package farijo.com.starcraft_bo_shower.network.custom_server;

import java.util.HashMap;

import farijo.com.starcraft_bo_shower.file_explorer.BOExplorerActivity;
import farijo.com.starcraft_bo_shower.file_explorer.BOFileAdapter;
import farijo.com.starcraft_bo_shower.file_explorer.VirtualFile;

/**
 * Created by Teddy on 11/02/2018.
 */

public class FileSynchronizer extends Thread {

    private static final String ROOT_NAME = "public";

    public HashMap<Short, BOFileAdapter> activeAdapters = new HashMap<>();

    private final VirtualFile fileSystem;
    private final BOExplorerActivity wrappingActivity;
    private String toStart = null;

    public FileSynchronizer(BOExplorerActivity wrappingActivity) {
        this.wrappingActivity = wrappingActivity;

        fileSystem = VirtualFile.loadVirtualFilesLocals(wrappingActivity);
    }

    public VirtualFile getFileSystem() {
        return fileSystem;
    }

    public void resetLaunchRequest() {
        toStart = null;
    }

    @Override
    public void run() {
        addRootFragmentToActivity();
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
}
