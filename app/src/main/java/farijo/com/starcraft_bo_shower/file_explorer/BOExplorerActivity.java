package farijo.com.starcraft_bo_shower.file_explorer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import farijo.com.starcraft_bo_shower.R;
import farijo.com.starcraft_bo_shower.player.BOActivity;

import static farijo.com.starcraft_bo_shower.file_explorer.ExplorerLevelFragment.ARG_PATH_KEY;

public class BOExplorerActivity extends AppCompatActivity {

    static final String ROOT_NAME = "public";

    VirtualFile fileSystem;

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

        fileSystem = VirtualFile.loadVirtualFilesLocals(this);

        fragPath = findViewById(R.id.frag_path);
        ImageView prevFolder = findViewById(R.id.minus_one);
        Picasso.with(this).load(R.drawable.parent_folder).fit().into(prevFolder);
        prevFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastFragment();
            }
        });

        showIpPortDialog();

        new Thread() {
            @Override
            public void run() {
                downloaderStarted = true;
                try {
                    synchronized (filesToDownload) {
                        while (filesToDownload.isEmpty()) {
                            if (destroyed) {
                                if (socket != null) {
                                    socket.close();
                                }
                                return;
                            }
                            filesToDownload.wait();
                        }
                        final String path = filesToDownload.poll();
                        final File file = new File(new File(getFilesDir(), "files"), path);
                        socket.getOutputStream().write(ByteBuffer.wrap((path + '\n').getBytes()).array());
                        byte[] filebytes = putSizeByteIntoBuffer(socket.getInputStream());
                        if(filebytes != null) {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(filebytes);
                            fileOutputStream.close();
                            file.setLastModified(readDate(socket.getInputStream()));
                        }
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

    private void showIpPortDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.server_chooser, null);
        final EditText ipServer = dialogView.findViewById(R.id.ip_server);
        final NumberPicker portServer = dialogView.findViewById(R.id.port_server);
        portServer.setMaxValue(65535);
        ipServer.setInputType(InputType.TYPE_CLASS_NUMBER);
        ipServer.setText("192.168.1.40");
        portServer.setValue(4040);
        new AlertDialog.Builder(this)
                .setTitle("IP & port")
                .setCancelable(false)
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startConnection(ipServer.getText().toString(), portServer.getValue());
                    }
                })
                .show();
    }

    private void startConnection(final String ip, final int port) {
        socketIniter = new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), 10000);
                    List<String> fileList = new ArrayList<>();
                    List<Long> fileUpdateTime = new ArrayList<>();
                    String data;
                    do {
                        data = receiveSizeByteIntoString(socket.getInputStream());
                        if (data != null) {
                            fileList.add(data);
                            fileUpdateTime.add(readDate(socket.getInputStream()));
                        }
                    } while (data != null);
                    fileSystem.loadVirtualFilesFromStrings(
                            fileList.toArray(new String[fileList.size()]),
                            fileUpdateTime.toArray(new Long[fileUpdateTime.size()])
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(!destroyed) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addFragment(ROOT_NAME);
                            }
                        });
                    }
                }
            }
        };
        socketIniter.start();
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
        TextView pathName = inflated.findViewById(R.id.path_name);
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

    public static byte[] putSizeByteIntoBuffer(InputStream inputStream) throws IOException {
        int size = readShort(inputStream);
        if (size <= 0) {
            return null;
        }
        byte[] dataFile = new byte[size];
        inputStream.read(dataFile);
        return dataFile;
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

    public static long readDate(InputStream inputStream) throws IOException{
        byte[] data = new byte[12];
        inputStream.read(data);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        calendar.set(Calendar.YEAR, buffer.getShort(0));
        calendar.set(Calendar.MONTH, buffer.getShort(2)-1);
        calendar.set(Calendar.DAY_OF_MONTH, buffer.getShort(4));
        calendar.set(Calendar.HOUR_OF_DAY, buffer.getShort(6));
        calendar.set(Calendar.MINUTE, buffer.getShort(8));
        calendar.set(Calendar.SECOND, buffer.getShort(10));
        return calendar.getTimeInMillis();
    }
}

