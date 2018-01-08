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

        RecyclerView recyclerView = ((RecyclerView) v.findViewById(R.id.explorer_level));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final String[] data = activity.fileSystem.getSubFiles(fullPath);
        if(data != null) {
            Arrays.sort(data, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    final int indexIfFile1 = o1.lastIndexOf('.');
                    final int indexIfFile2 = o2.lastIndexOf('.');
                    if(indexIfFile1 >= 0) {
                        if(indexIfFile2 >=0) {
                            return o1.substring(0, indexIfFile1).compareTo(o2.substring(0, indexIfFile2));
                        } else {
                            return 1;
                        }
                    } else {
                        if(indexIfFile2 >= 0) {
                            return -1;
                        } else {
                            return o1.compareTo(o2);
                        }
                    }
                }
            });
            recyclerView.setAdapter(new BOFileAdapter(activity, this, null));
        }

        return v;
    }

}
