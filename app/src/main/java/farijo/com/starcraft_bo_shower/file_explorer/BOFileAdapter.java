package farijo.com.starcraft_bo_shower.file_explorer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import farijo.com.starcraft_bo_shower.R;

/**
 * Created by Teddy on 30/12/2017.
 */

public class BOFileAdapter extends RecyclerView.Adapter<BOFileAdapter.ViewHolder> {
    private static final int FOLDER_TYPE = 2;
    private static final int ONLINE_FILE_TYPE = 3;
    private static final int LOCAL_FILE_TYPE = 4;

    private BOExplorerActivity activity;
    private ExplorerLevelFragment fragment;
    private BOExplorerActivity.VirtualFile[] data;

    BOFileAdapter(BOExplorerActivity a, ExplorerLevelFragment f, BOExplorerActivity.VirtualFile[] d) {
        activity = a;
        fragment = f;
        data = d;
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        public abstract void setData(BOExplorerActivity.VirtualFile path);
    }

    class FolderViewHolder extends ViewHolder {

        FolderViewHolder(View itemView) {
            super(itemView);
            ImageView folderIcon = (ImageView) itemView.findViewById(R.id.folder_icon);
            Picasso.with(itemView.getContext()).load(R.drawable.folder).fit().into(folderIcon);
        }

        @Override
        public void setData(BOExplorerActivity.VirtualFile path) {
            final String fPath = path.fileName;
            name.setText(fPath);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fragment.canProceed) {
                        fragment.canProceed = false;
                        activity.addFragment(fPath);
                    }
                }
            });
        }
    }

    class FileViewHolder extends ViewHolder {
        View btnStart;
        View btnDownload;

        FileViewHolder(View itemView) {
            super(itemView);
            btnStart = itemView.findViewById(R.id.launch);
            btnDownload = itemView.findViewById(R.id.download);
        }

        @Override
        public void setData(BOExplorerActivity.VirtualFile path) {
            final String fPath = path.fileName;
            name.setText(fPath);
            final String fullPathPath = fragment.fullPath+"/"+fPath;
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.toStart = fullPathPath;
                    synchronized (activity.filesToDownload) {
                        activity.filesToDownload.add(fullPathPath);
                        activity.filesToDownload.notify();
                    }
                }
            });
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    synchronized (activity.filesToDownload) {
                        activity.filesToDownload.add(fullPathPath);
                        activity.filesToDownload.notify();
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data[position].isFile() ? (data[position].local ? LOCAL_FILE_TYPE : ONLINE_FILE_TYPE) : FOLDER_TYPE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOLDER_TYPE:
                return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_folder, parent, false));
            case ONLINE_FILE_TYPE:
                return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}