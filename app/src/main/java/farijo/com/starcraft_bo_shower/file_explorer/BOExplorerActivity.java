package farijo.com.starcraft_bo_shower.file_explorer;

import android.content.DialogInterface;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import farijo.com.starcraft_bo_shower.R;
import farijo.com.starcraft_bo_shower.network.FileSynchronizer;

import static farijo.com.starcraft_bo_shower.file_explorer.ExplorerLevelFragment.ARG_PATH_KEY;

public class BOExplorerActivity extends AppCompatActivity {

    public FileSynchronizer synchronizer;
    private List<ExplorerLevelFragment> fragmentsList = new ArrayList<>();
    private LinearLayout fragPath;
    private boolean isDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boexplorer);

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
    }

    private void showIpPortDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.server_chooser, null);
        final EditText ipServer = dialogView.findViewById(R.id.ip_server);
        final NumberPicker portServer = dialogView.findViewById(R.id.port_server);
        portServer.setMaxValue(Short.MAX_VALUE - Short.MIN_VALUE);
        ipServer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
        ipServer.setText("192.168.1.20");
        portServer.setValue(4040);
        new AlertDialog.Builder(this)
                .setTitle("IP & port")
                .setCancelable(false)
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synchronizer = new FileSynchronizer(
                                BOExplorerActivity.this,
                                ipServer.getText().toString(),
                                portServer.getValue()
                        );
                        synchronizer.start();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        synchronized (synchronizer) {
            synchronizer.notify();
        }
        super.onDestroy();
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
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
}

