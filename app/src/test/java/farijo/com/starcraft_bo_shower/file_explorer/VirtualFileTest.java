package farijo.com.starcraft_bo_shower.file_explorer;

import android.util.Log;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Teddy on 07/01/2018.
 */

public class VirtualFileTest {
    @Test
    public void putGetTest() throws Exception {
        VirtualFile virtualFile = new VirtualFile("filename", false);
        VirtualFile sonDepth1 = new VirtualFile("filsD1", false);
        VirtualFile sonDepth2 = new VirtualFile("filsD2", false);
        VirtualFile sonDepth2_2 = new VirtualFile("filsD2_2", true);
        VirtualFile sonDepth3 = new VirtualFile("filsD3", true);
        VirtualFile sonDepth3_r = new VirtualFile("filsD3", false);

        assertNull(virtualFile.get("yo"));
        assertFalse(virtualFile.isFile());

        virtualFile.put(sonDepth1);
        virtualFile.get(sonDepth1.fileName).put(sonDepth2);
        virtualFile.get(sonDepth1.fileName).put(sonDepth2_2);
        virtualFile.get(sonDepth1.fileName).get(sonDepth2.fileName).put(sonDepth3);
        virtualFile.get(sonDepth1.fileName).get(sonDepth2.fileName).put(sonDepth3_r);

        assertEquals(virtualFile.fileName, "filename");
        assertEquals(sonDepth1, virtualFile.get("filsD1"));
        assertEquals(sonDepth2, virtualFile.get("filsD1").get("filsD2"));
        assertEquals(sonDepth2_2, virtualFile.get("filsD1").get("filsD2_2"));
        assertEquals(sonDepth3, virtualFile.get("filsD1").get("filsD2").get("filsD3"));
    }

    @Test
    public void getSubFilesTest() throws Exception {
        VirtualFile virtualFile = new VirtualFile("rootFolder", false);
        VirtualFile sonDepth1 = new VirtualFile("filsD1", false);
        VirtualFile sonDepth2 = new VirtualFile("filsD2", false);
        VirtualFile sonDepth2_2 = new VirtualFile("filsD2_2", true);
        VirtualFile sonDepth3 = new VirtualFile("filsD3", true);
        VirtualFile sonDepth3_r = new VirtualFile("filsD3", false);

        virtualFile.put(sonDepth1);
        virtualFile.get(sonDepth1.fileName).put(sonDepth2);
        virtualFile.get(sonDepth1.fileName).put(sonDepth2_2);
        virtualFile.get(sonDepth1.fileName).get(sonDepth2.fileName).put(sonDepth3);
        virtualFile.get(sonDepth1.fileName).get(sonDepth2.fileName).put(sonDepth3_r);

        assertArrayEquals(new VirtualFile[]{sonDepth2, sonDepth2_2}, virtualFile.getSubFiles("filsD1"));
        assertArrayEquals(new VirtualFile[]{sonDepth3}, virtualFile.getSubFiles("filsD1/filsD2"));
        assertArrayEquals(new VirtualFile[]{sonDepth1}, virtualFile.getSubFiles("/"));
        assertArrayEquals(new VirtualFile[]{sonDepth1}, virtualFile.getSubFiles("//"));
        assertArrayEquals(new VirtualFile[]{sonDepth1}, virtualFile.getSubFiles(""));
    }

    @Test
    public void loadVirtualFilesFromStringsTest() throws Exception {
        VirtualFile virtualFile = new VirtualFile("filename", false);

        String[] paths = new String[] {
                "coucou/oui/non/y",
                "coucou/non/fichier",
                "coucou/non//fichier",
                "coucou",
                "coucou/oui/non/y/fichier",
                "coucou/oui/non/y",
                "coucou/oui/non/y/pasfichier/fichier",
        };

        virtualFile.loadVirtualFilesFromStrings(paths);

        assertFalse(virtualFile.get("coucou").isFile());
        assertFalse(virtualFile.get("coucou").get("oui").isFile());
        assertFalse(virtualFile.get("coucou").get("oui").get("non").isFile());
        assertFalse(virtualFile.get("coucou").get("oui").get("non").get("y").isFile());
        assertFalse(virtualFile.get("coucou").get("non").isFile());
        assertTrue(virtualFile.get("coucou").get("non").get("fichier").isFile());
        assertTrue(virtualFile.get("coucou").get("non").get("").get("fichier").isFile());
        assertTrue(virtualFile.get("coucou").get("oui").get("non").get("y").get("fichier").isFile());
        assertFalse(virtualFile.get("coucou").get("oui").get("non").get("y").get("pasfichier").isFile());
        assertTrue(virtualFile.get("coucou").get("oui").get("non").get("y").get("pasfichier").get("fichier").isFile());
    }
}
