package ai.magicmirror.magicmirror.features.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.magicmirror.magicmirror.R;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverHolder>{

    @NonNull
    @Override
    public DiscoverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_discover_fragment_recycler_view_item, parent, false);

        return new DiscoverHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 50;
    }

    public class DiscoverHolder extends RecyclerView.ViewHolder{

        public DiscoverHolder(View itemView) {
            super(itemView);
        }
    }
}
