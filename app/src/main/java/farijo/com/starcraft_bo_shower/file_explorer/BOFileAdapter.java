package farijo.com.starcraft_bo_shower.file_explorer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import farijo.com.starcraft_bo_shower.R;

/**
 * Created by Teddy on 30/12/2017.
 */

public class BOFileAdapter extends RecyclerView.Adapter<BOFileAdapter.ViewHolder> {
    private static final int FOLDER_TYPE = 2;
    private static final int ONLINE_FILE_TYPE = 3;
    private static final int LOCAL_FILE_TYPE = 4;
    private static final int LOCAL_FILE_UPDATABLE_TYPE = 5;
    private static final int FILE_DOWNLOADING_TYPE = 6;

    private static short IDs = 0;

    private BOExplorerActivity activity;
    private ExplorerLevelFragment fragment;
    private VirtualFile[] data;
    final short ID;

    BOFileAdapter(BOExplorerActivity a, ExplorerLevelFragment f, VirtualFile[] d) {
        activity = a;
        fragment = f;
        data = d;
        ID = IDs++;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }

        public void setData(final VirtualFile file) {
            name.setText(file.fileName);
        }
    }

    class FolderViewHolder extends ViewHolder {

        FolderViewHolder(View itemView) {
            super(itemView);
            ImageView folderIcon = itemView.findViewById(R.id.folder_icon);
            Picasso.with(itemView.getContext()).load(R.drawable.folder).fit().into(folderIcon);
        }

        @Override
        public void setData(final VirtualFile file) {
            super.setData(file);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fragment.canProceed) {
                        fragment.canProceed = false;
                        activity.addFragment(file.fileName);
                    }
                }
            });
        }
    }

    class FileViewHolder extends ViewHolder {
        View btnStart;

        FileViewHolder(View itemView) {
            super(itemView);
            btnStart = itemView.findViewById(R.id.launch_local);
        }

        @Override
        public void setData(final VirtualFile file) {
            super.setData(file);
            final String fullPathPath = fragment.fullPath + "/" + file.fileName;
            if (btnStart != null) {
                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.synchronizer.resetLaunchRequest();
                        activity.startBO(new File(new File(activity.getFilesDir(), "files"), fullPathPath).getAbsolutePath());
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(data[position].isFile()) {
            if(data[position].isDownloading()) {
                return FILE_DOWNLOADING_TYPE;
            } else {
                if (data[position].isLocal) {
                    if (data[position].updateAvailable) {
                        return LOCAL_FILE_UPDATABLE_TYPE;
                    } else {
                        return LOCAL_FILE_TYPE;
                    }
                } else {
                    return ONLINE_FILE_TYPE;
                }
            }
        } else {
            return FOLDER_TYPE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOLDER_TYPE:
                return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_folder, parent, false));
            case ONLINE_FILE_TYPE:
                return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
            case LOCAL_FILE_UPDATABLE_TYPE:
                return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_updatable, parent, false));
            case LOCAL_FILE_TYPE:
                return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_locale, parent, false));
            case FILE_DOWNLOADING_TYPE:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_downloading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(data[position]);
    }

    public void notifyChanged(VirtualFile file) {
        for (int i = 0; i < data.length; i++) {
            if(data[i] == file) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}