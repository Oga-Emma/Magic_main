package ai.magicmirror.magicmirror.FEATURES.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ai.magicmirror.magicmirror.DAOs.ImageLikesDAO;
import ai.magicmirror.magicmirror.DTO.OriginalImageDTO;
import ai.magicmirror.magicmirror.GlideApp;
import ai.magicmirror.magicmirror.R;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverHolder>{

    private List<OriginalImageDTO> list;
    private Context context;

    public DiscoverAdapter(List<OriginalImageDTO> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DiscoverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_discover_fragment_recycler_view_item, parent, false);

        this.context = parent.getContext();
        return new DiscoverHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverHolder holder, int position) {
        holder.bindView(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DiscoverHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final View view;
        private ImageView imageView;
        private OriginalImageDTO imageDTO;

        public DiscoverHolder(View itemView) {
            super(itemView);

            this.view = itemView;

            imageView = itemView.findViewById(R.id.discover_image_view);
            imageView.setOnClickListener(this::onClick);
        }

        public void bindView(OriginalImageDTO imageDTO) {
            this.imageDTO = imageDTO;

            GlideApp.with(context)
                    .load(imageDTO.getImageUrl())
                    .placeholder(R.color.cardview_dark_background)
                    .thumbnail(1.0f)
                    .into(imageView);

            GlideApp.with(context)
                    .load(imageDTO.getImageUrl())
                    .preload();
        }

        @Override
        public void onClick(View v) {
            context.startActivity(new Intent(context, ImagePreviewActivity.class));
            ImagePreviewActivity.launchActivity(((Activity) context), imageDTO, view);
        }
    }
}
