package farijo.com.starcraft_bo_shower.file_explorer;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Teddy on 07/01/2018.
 */

public class VirtualFile {
    public final String fileName;
    boolean isLocal;
    private boolean isFile;
    boolean updateAvailable;
    private long lastModif;
    private Map<String, VirtualFile> subFiles = new HashMap<>();

    private boolean downloading = false;

    VirtualFile(String fileName, boolean isFile) {
        this(fileName, false, isFile, 0);
    }

    private VirtualFile(String fileName, boolean isLocal, boolean isFile, long lastModif) {
        this.fileName = fileName;
        this.isLocal = isLocal;
        this.isFile = isFile;
        this.lastModif = lastModif;
    }

    private void setFolder() {
        isFile = false;
    }

    boolean isFile() {
        return isFile;
    }

    private boolean containsKey(String key) {
        if(isFile) {
            return false;
        }
        return subFiles.containsKey(key);
    }

    VirtualFile get(String key) {
        if(isFile) {
            return null;
        }
        return subFiles.get(key);
    }

    VirtualFile put(VirtualFile vf) {
        setFolder();
        if(containsKey(vf.fileName)) {
            VirtualFile existingSon = get(vf.fileName);
            for (VirtualFile virtualFile : vf.subFiles.values()) {
                existingSon.put(virtualFile);
            }
            return existingSon;
        } else {
            return subFiles.put(vf.fileName, vf);
        }
    }

    public void startDownload() {
        downloading = true;
    }

    public void endDownload() {
        downloading = false;
        isLocal = true;
        updateAvailable = false;
    }

    public void endDownloadEmpty() {
        downloading = false;
    }

    boolean isDownloading() {
        return downloading;
    }

    VirtualFile[] getSubFiles(String fullPath) {
        VirtualFile actualVF = this;
        if(!fullPath.isEmpty()) {
            for (String s : fullPath.split("/")) {
                if (!actualVF.containsKey(s)) {
                    return null;
                }
                actualVF = actualVF.get(s);
            }
        }
        VirtualFile res[] = new VirtualFile[actualVF.subFiles.size()];
        actualVF.subFiles.values().toArray(res);
        return res;
    }

    public void loadVirtualFilesFromStrings(String files[], Long timeUpdate[]) {
        for (int i = 0; i < files.length; i++) {
            VirtualFile actualVF = this;
            for (String s : files[i].split("/")) {
                if (!actualVF.containsKey(s)) {
                    actualVF.put(new VirtualFile(s, true));
                } else {
                    if(timeUpdate[i] > actualVF.get(s).lastModif) {
                        actualVF.get(s).updateAvailable = true;
                        actualVF.get(s).lastModif = timeUpdate[i];
                    }
                }
                actualVF = actualVF.get(s);
            }
        }
    }

    public static VirtualFile loadVirtualFilesLocals(Context context) {
        return loadVirtualFilesLocals(context, null);
    }

    private static VirtualFile loadVirtualFilesLocals(Context context, VirtualFile res) {
        if(res == null) {
            res = new VirtualFile(".root", false);
        }
        parseFolder(new File(context.getFilesDir(), "files/public"), res);
        return res;
    }

    private static void parseFolder(File folder, VirtualFile parent) {
        if(!parent.containsKey(folder.getName())) {
            parent.put(new VirtualFile(folder.getName(), true, folder.isFile(), folder.lastModified()));
        }
        File[] files = folder.listFiles();
        if(files != null && files.length > 0) {
            for (File file : files) {
                parseFolder(file, parent.get(folder.getName()));
            }
        }
    }
}
