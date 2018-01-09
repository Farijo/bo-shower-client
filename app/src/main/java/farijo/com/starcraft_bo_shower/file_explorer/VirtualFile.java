package farijo.com.starcraft_bo_shower.file_explorer;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Teddy on 07/01/2018.
 */

class VirtualFile {
    final String fileName;
    final boolean local;
    private boolean isFile;
    boolean updateAvailable;
    private long lastModif;
    private Map<String, VirtualFile> subFiles = new HashMap<>();

    VirtualFile(String n, boolean isF) {
        this(n, false, isF, 0);
    }

    private VirtualFile(String n, boolean l, boolean isF, long lastm) {
        fileName = n;
        local = l;
        isFile = isF;
        lastModif = lastm;
    }

    @Override
    public String toString() {
        return (isFile()?"file":"directory")+":"+subFiles.toString();
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
            for (VirtualFile virtualFile : vf.subFiles.values()) {
                get(vf.fileName).put(virtualFile);
            }
            return get(vf.fileName);
        } else {
            return subFiles.put(vf.fileName, vf);
        }
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

    static VirtualFile loadVirtualFilesLocals(Context context) {
        return loadVirtualFilesLocals(context, null);
    }

    static VirtualFile loadVirtualFilesLocals(Context context, VirtualFile res) {
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

    void loadVirtualFilesFromStrings(String files[]) {
        for (String file : files) {
            VirtualFile actualVF = this;
            for (String s : file.split("/")) {
                if (!actualVF.containsKey(s)) {
                    actualVF.put(new VirtualFile(s, true));
                } else {
                    actualVF.get(s).updateAvailable = true;
                }
                actualVF = actualVF.get(s);
            }
        }
    }
}
