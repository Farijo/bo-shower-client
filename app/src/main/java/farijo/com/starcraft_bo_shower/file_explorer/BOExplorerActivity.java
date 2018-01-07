package farijo.com.starcraft_bo_shower.file_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import farijo.com.starcraft_bo_shower.R;
import farijo.com.starcraft_bo_shower.player.BOActivity;

import static farijo.com.starcraft_bo_shower.file_explorer.ExplorerLevelFragment.ARG_PATH_KEY;

public class BOExplorerActivity extends AppCompatActivity {

    private static final String ROOT_NAME = "public";

    static class VirtualFile {
        final String fileName;
        boolean local;
        boolean updateAvailable;
        long lastModif;
        private Map<String, VirtualFile> subFiles = null;

        VirtualFile(String n) {
            fileName = n;
        }

        VirtualFile(String n, boolean l, long lastm) {
            fileName = n;
            local = l;
            lastModif = lastm;
        }

        void init() {
            if (subFiles == null) {
                subFiles = new HashMap<>();
            }
        }

        boolean isFile() {
            return subFiles == null;
        }

        boolean containsKey(String key) {
            return subFiles.containsKey(key);
        }

        VirtualFile get(String key) {
            return subFiles.get(key);
        }

        VirtualFile put(String key, VirtualFile vf) {
            return subFiles.put(key, vf);
        }
    }

    VirtualFile fileSystem = new VirtualFile(ROOT_NAME);

    private boolean destroyed = false;
    String toStart = null;

    final ConcurrentLinkedQueue<String> filesToDownload = new ConcurrentLinkedQueue<>();

    private Thread socketIniter;
    private boolean downloaderStarted = false;
    private Socket socket;

    private List<ExplorerLevelFragment> fragmentsList = new ArrayList<>();
    private LinearLayout fragPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boexplorer);

        fragPath = (LinearLayout) findViewById(R.id.frag_path);
        ImageView prevFolder = (ImageView) findViewById(R.id.minus_one);
        Picasso.with(this).load(R.drawable.parent_folder).fit().into(prevFolder);
        prevFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastFragment();
            }
        });

        loadVirtualFilesLocals();
        addFragment(ROOT_NAME);

        socketIniter = new Thread() {
            @Override
            public void run() {
                try {
                    List<String> fileList = new ArrayList<>();
                    socket = new Socket("192.168.1.22", 4040);
                    String data;
                    do {
                        data = receiveSizeByteIntoString(socket.getInputStream());
                        if (data != null) {
                            fileList.add(data);
                        }
                    } while (data != null);
                    loadVirtualFilesFromStrings(fileList.toArray(new String[fileList.size()]));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addFragment(ROOT_NAME);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        socketIniter.start();

        new Thread() {
            @Override
            public void run() {
                downloaderStarted = true;
                try {
                    synchronized (filesToDownload) {
                        while (filesToDownload.isEmpty()) {
                            if (destroyed) {
                                if(socket != null) {
                                    socket.close();
                                }
                                return;
                            }
                            filesToDownload.wait();
                        }
                        final String path = filesToDownload.poll();
                        final File file = new File(getFilesDir(), path);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        socket.getOutputStream().write(ByteBuffer.wrap((path + '\n').getBytes()).array());
                        putSizeByteIntoBuffer(socket.getInputStream(), new FileOutputStream(file));
                        file.setLastModified(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), file.getName() + " downloaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (path.equals(toStart)) {
                            toStart = null;
                            Intent intent = new Intent(BOExplorerActivity.this, BOActivity.class);
                            intent.putExtra(BOActivity.BO_EXTRA, file.getAbsolutePath());
                            startActivity(intent);
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                run();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        destroyed = true;
        if(downloaderStarted) {
            synchronized (filesToDownload) {
                filesToDownload.notify();
            }
        } else {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        socketIniter.interrupt();
        super.onDestroy();
    }

    public void addFragment(String path) {

        View inflated = LayoutInflater.from(this).inflate(R.layout.one_path, fragPath, false);
        TextView pathName = (TextView) inflated.findViewById(R.id.path_name);
        pathName.setText(path);
        final int index = fragmentsList.size() + 1;
        pathName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllFragmentsFrom(index);
            }
        });
        fragPath.addView(inflated);

        ExplorerLevelFragment newFrag = new ExplorerLevelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH_KEY, fragmentsList.isEmpty() ? path : fragmentsList.get(fragmentsList.size() - 1).fullPath + "/" + path);
        newFrag.setArguments(args);

        fragmentsList.add(newFrag);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0)
                .add(R.id.container, newFrag)
                .commit();
    }

    public void removeLastFragment() {
        if (fragmentsList.size() < 2) {
            return;
        }
        removeAllFragmentsFrom(fragmentsList.size() - 1);
    }

    public void removeAllFragmentsFrom(int firstIndex) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(0, R.anim.slide_out_right);
        for (int i = fragmentsList.size() - 1; i >= firstIndex; i--) {
            transaction = transaction.remove(fragmentsList.remove(i));
            fragPath.removeViewAt(i);
        }
        if (firstIndex > 0) {
            fragmentsList.get(firstIndex - 1).canProceed = true;
        }
        transaction.commit();
    }

    private void loadVirtualFilesLocals() {
        List<String> locals = new ArrayList<>();
        parseFolder(getFilesDir(), locals, fileSystem);
    }

    private void parseFolder(File folder, List<String> res, VirtualFile parent) {
        parent.init();
        if(folder.isFile()) {
            parent.put(folder.getName(), new VirtualFile(folder.getName(), true, folder.lastModified()));
            return;
        }
        for (File file : folder.listFiles()) {
            if(!parent.containsKey(file.getName())) {
                parent.put(file.getName(), new VirtualFile(file.getName(), true, file.lastModified()));
            }
            parseFolder(file, res, parent.get(file.getName()));
        }
    }

    private void loadVirtualFilesFromStrings(String files[]) {
        VirtualFile actualVF;

        for (String file : files) {
            if (file.lastIndexOf('.') <= file.lastIndexOf('/')) {
                continue;
            }
            actualVF = fileSystem;
            for (String s : file.split("/")) {
                actualVF.init();
                if (!actualVF.containsKey(s)) {
                    actualVF.put(s, new VirtualFile(s));
                }
                actualVF = actualVF.get(s);
            }
        }
    }

    public String[] getSubFiles(String fullPath) {
        VirtualFile actualVF = fileSystem;
        for (String s : fullPath.split("/")) {
            if (!actualVF.containsKey(s)) {
                continue;
            }
            actualVF = actualVF.get(s);
        }
        if (actualVF.subFiles == null) {
            return null;
        }
        String res[] = new String[actualVF.subFiles.size()];
        actualVF.subFiles.keySet().toArray(res);
        return res;
    }

    public static void putSizeByteIntoBuffer(InputStream inputStream, OutputStream outputStream) throws IOException {
        int size = readShort(inputStream);
        if (size <= 0) {
            return;
        }
        byte[] dataFile = new byte[size];
        inputStream.read(dataFile);
        outputStream.write(dataFile);
        outputStream.close();
    }

    public static String receiveSizeByteIntoString(InputStream stream) throws IOException {
        byte[] dataSize = new byte[2];
        stream.read(dataSize);
        int size = ByteBuffer.wrap(dataSize).order(ByteOrder.BIG_ENDIAN).getShort();
        if (size <= 0) {
            return null;
        }
        byte[] dataFileName = new byte[size];
        stream.read(dataFileName, 0, size);
        return new String(dataFileName, 0, size);
    }

    public static short readShort(InputStream inputStream) throws IOException{
        byte[] dataSize = new byte[2];
        inputStream.read(dataSize);
        return ByteBuffer.wrap(dataSize).order(ByteOrder.BIG_ENDIAN).getShort();
    }
}

