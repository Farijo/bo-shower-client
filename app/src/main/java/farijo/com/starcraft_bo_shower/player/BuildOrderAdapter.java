package farijo.com.starcraft_bo_shower.player;

import android.graphics.Color;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import farijo.com.starcraft_bo_shower.R;

/**
 * Created by Teddy on 21/07/2017.
 */

public class BuildOrderAdapter extends RecyclerView.Adapter<BuildOrderAdapter.ViewHolder> {

    private List<SC2Action> actions = new ArrayList<>();
    private boolean started = false;
    private boolean timingsOk = true;
    private boolean showTimer = true;

    public void disableTimings() {
        timingsOk = false;
    }

    public void setActions(List<SC2Action> a) {
        actions = a;
        notifyDataSetChanged();
    }

    public void addAction(SC2Action action) {
        actions.add(action);
        notifyItemInserted(actions.size()-1);
    }

    public void showTimers(boolean show) {
        if(show != showTimer) {
            showTimer = show;
            notifyItemRangeChanged(0, actions.size());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        static int LIGHT = Color.argb(255, 60, 60, 60);
        static int DARK = Color.argb(255, 40, 40, 40);

        TextView pop;
        ImageView onFinishAction;
        View finishFlag;
        TextView time;
        ImageView icon;
        TextView count;
        TextView name;
        TextView details;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            pop = itemView.findViewById(R.id.pop);
            onFinishAction = itemView.findViewById(R.id.onfinish_icon);
            finishFlag = itemView.findViewById(R.id.onfinish_flag);
            time = itemView.findViewById(R.id.time);
            icon = itemView.findViewById(R.id.icon);
            count = itemView.findViewById(R.id.count);
            name = itemView.findViewById(R.id.name);
            details = itemView.findViewById(R.id.details);

            if(viewType == 0) {
                itemView.setBackgroundColor(LIGHT);
            } else {
                itemView.setBackgroundColor(DARK);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position%2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_construction, null), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SC2Action act = actions.get(position);
        act.view = holder.itemView;

        holder.pop.setText(act.population);
        if(act.isDepedentOfEntity()) {
            holder.finishFlag.setVisibility(View.VISIBLE);
            holder.onFinishAction.setImageResource(actions.get(act.onFinish).resourceIcon);
        }
        if(timingsOk && showTimer) {
            holder.time.setText(act.strTiming);
            holder.time.setVisibility(View.VISIBLE);
        } else {
            holder.time.setVisibility(View.GONE);
        }
        holder.icon.setImageResource(act.resourceIcon);
        if(act.count > 1) {
            holder.count.setText(String.valueOf(act.count));
        }
        holder.name.setText(act.name);
        holder.details.setText(act.details);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public void startTimer(final View timer, final NestedScrollView scroller, final CheckBox checkBox, final BOActivity BOActivity) throws NoSuchElementException {
        if(!started) {
            Iterator<SC2Action> it = actions.iterator();
            new Progresser(it.next(), it, timer, scroller, checkBox, new WeakReference<>(BOActivity)).start();
        } else {
            BOActivity.currentProgress.cancel();
            BOActivity.currentProgress.rollBackColor();
            timer.getLayoutParams().height = 0;
            timer.requestLayout();
        }
        started = !started;
    }
}
