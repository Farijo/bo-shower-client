package farijo.com.starcraft_bo_shower;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import farijo.com.starcraft_bo_shower.player.BOActivity;

/**
 * Created by Teddy on 22/07/2017.
 */

public class BuildListAdapter extends RecyclerView.Adapter<BuildListAdapter.ViewHolder> {

    private final List<String> buildOrders = new ArrayList<>();
    private Context context;

    public BuildListAdapter(Context c) {
        context = c;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = ((TextView) itemView.findViewById(R.id.name));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_bo, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String name = buildOrders.get(position);
        holder.name.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(context, BOActivity.class);
                data.putExtra(BOActivity.BO_EXTRA, name);
                context.startActivity(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildOrders.size();
    }

    public void add(String s) {
        buildOrders.add(s);
        notifyItemInserted(buildOrders.size()-1);
    }
}
