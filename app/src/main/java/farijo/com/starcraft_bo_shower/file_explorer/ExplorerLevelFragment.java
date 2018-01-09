package farijo.com.starcraft_bo_shower.file_explorer;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Comparator;

import farijo.com.starcraft_bo_shower.R;

public class ExplorerLevelFragment extends Fragment {

    static final String ARG_PATH_KEY = "fullPath";

    private BOExplorerActivity activity;
    String fullPath;
    boolean canProceed = true;

    public ExplorerLevelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BOExplorerActivity) getActivity());
        fullPath = getArguments().getString(ARG_PATH_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explorer_level, container, false);

        final int greyIntensity = 48+fullPath.replaceAll("[^/]", "").length()*15;
        v.setBackgroundColor(Color.rgb(48, 48, 48));

        RecyclerView recyclerView = v.findViewById(R.id.explorer_level);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final VirtualFile[] data = activity.fileSystem.getSubFiles(fullPath);
        if(data != null) {
            Arrays.sort(data, new Comparator<VirtualFile>() {
                @Override
                public int compare(VirtualFile o1, VirtualFile o2) {
                    if(o1.isFile()) {
                        if(o2.isFile()) {
                            return o1.fileName.compareTo(o2.fileName);
                        } else {
                            return 1;
                        }
                    } else {
                        if(o2.isFile()) {
                            return -1;
                        } else {
                            return o1.fileName.compareTo(o2.fileName);
                        }
                    }
                }
            });
            recyclerView.setAdapter(new BOFileAdapter(activity, this, data));
        }

        return v;
    }

}
