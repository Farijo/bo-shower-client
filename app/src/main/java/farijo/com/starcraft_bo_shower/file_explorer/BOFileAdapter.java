package farijo.com.starcraft_bo_shower.file_explorer;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import farijo.com.starcraft_bo_shower.R;
import farijo.com.starcraft_bo_shower.player.BOActivity;

/**
 * Created by Teddy on 30/12/2017.
 */

public class BOFileAdapter extends RecyclerView.Adapter<BOFileAdapter.ViewHolder> {
    private static final int FOLDER_TYPE = 2;
    private static final int ONLINE_FILE_TYPE = 3;
    private static final int LOCAL_FILE_TYPE = 4;
    private static final int LOCAL_FILE_UPDATABLE_TYPE = 5;

    private BOExplorerActivity activity;
    private ExplorerLevelFragment fragment;
    private VirtualFile[] data;

    BOFileAdapter(BOExplorerActivity a, ExplorerLevelFragment f, VirtualFile[] d) {
        activity = a;
        fragment = f;
        data = d;
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }

        public abstract void setData(VirtualFile path);
    }

    class FolderViewHolder extends ViewHolder {

        FolderViewHolder(View itemView) {
            super(itemView);
            ImageView folderIcon = itemView.findViewById(R.id.folder_icon);
            Picasso.with(itemView.getContext()).load(R.drawable.folder).fit().into(folderIcon);
        }

        @Override
        public void setData(VirtualFile path) {
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
        View btnDownloadStart;
        View btnDownload;
        View btnStart;

        FileViewHolder(View itemView) {
            super(itemView);
            btnDownloadStart = itemView.findViewById(R.id.download_launch);
            btnDownload = itemView.findViewById(R.id.download);
            btnStart = itemView.findViewById(R.id.launch_local);
        }

        @Override
        public void setData(VirtualFile path) {
            final String fPath = path.fileName;
            name.setText(fPath);
            final String fullPathPath = fragment.fullPath + "/" + fPath;
            if (btnDownloadStart != null) {
                btnDownloadStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.toStart = fullPathPath;
                        synchronized (activity.filesToDownload) {
                            activity.filesToDownload.add(fullPathPath);
                            activity.filesToDownload.notify();
                        }
                    }
                });
            }
            if (btnDownload != null) {
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
            if (btnStart != null) {
                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activity.toStart != null) {
                            activity.toStart = fullPathPath;
                            Toast.makeText(activity, "lancement de " + fPath + " dès que le prochain téléchargement termine", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(activity, BOActivity.class);
                            intent.putExtra(BOActivity.BO_EXTRA, new File(new File(activity.getFilesDir(), "files"), fullPathPath).getAbsolutePath());
                            activity.startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(data[position].isFile()) {
            if(data[position].local) {
                if(data[position].updateAvailable) {
                    return LOCAL_FILE_UPDATABLE_TYPE;
                } else {
                    return LOCAL_FILE_TYPE;
                }
            } else {
                return ONLINE_FILE_TYPE;
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