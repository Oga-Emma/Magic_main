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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import ai.magicmirror.magicmirror.DAOs.ImageLikesDAO;
import ai.magicmirror.magicmirror.DTO.OriginalImageDTO;
import ai.magicmirror.magicmirror.GlideApp;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.FEATURES.swap.SwapActivity;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedHolder>{

    private final String userId;
    Context context;
    private Activity activity;
    private List<OriginalImageDTO> imageDTOList;

    public FeedAdapter(Activity activity,  List<OriginalImageDTO> imageDTOList) {
        this.activity = activity;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.imageDTOList = imageDTOList;
    }

    @NonNull
    @Override
    public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_fragment_recycler_view_item, parent, false);

        this.context = parent.getContext();
        return new FeedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedHolder holder, int position) {

        holder.bindView(imageDTOList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageDTOList.size();
    }

    public class FeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ImageLikesDAO.ImageLikes {

        ImageView feedImage, swapImageView, likeimageView;
        TextView imageLikesTextView, imageSwapsTextView;
        private OriginalImageDTO imageDTO;

        public FeedHolder(View itemView) {
            super(itemView);

            feedImage = itemView.findViewById(R.id.feed_image_view);
            swapImageView = itemView.findViewById(R.id.swap_image_view);
            likeimageView = itemView.findViewById(R.id.like_image_image_view);
            imageLikesTextView = itemView.findViewById(R.id.image_likes_count_text_view);
            imageSwapsTextView = itemView.findViewById(R.id.image_swap_count_text_view);

            likeimageView.setOnClickListener(this::onClick);
            swapImageView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {

            if(view.getId() == R.id.like_image_image_view){
                ImageLikesDAO.getInstance(context)
                        .likeImage(imageDTO.getImageId(), userId,this);

            }else {
                Intent intent = new Intent(view.getContext(), SwapActivity.class);
                intent.putExtra(SwapActivity.SWAP_ACTIVITY_IMAGE_FROM_FEED_INTENT,
                        imageDTOList.get(getAdapterPosition()));

                view.getContext().startActivity(intent);
            }

        }

        public void bindView(OriginalImageDTO imageDTO) {
            this.imageDTO = imageDTO;

            ImageLikesDAO.getInstance(context)
                    .getImageLikesCount(imageDTO.getImageId(), this);

            ImageLikesDAO.getInstance(context)
                    .userLikedImage(imageDTO.getImageId(), userId, this);

            if(!imageDTO.getImageUrl().isEmpty()) {

                GlideApp.with(context)
                        .load(imageDTO.getImageUrl())
                        .placeholder(R.color.cardview_dark_background)
                        .into(feedImage);
            }

        }

        @Override
        public void imageLiked(int totalLikeCount) {
            imageLikesTextView.setText(String.valueOf(totalLikeCount));
            likeimageView.setImageDrawable(context.getResources().getDrawable(R.drawable.like_image));

        }

        @Override
        public void imageDisliked(int totalLikeCount) {
            imageLikesTextView.setText(String.valueOf(totalLikeCount));
            likeimageView.setImageDrawable(context.getResources().getDrawable(R.drawable.unlike_image));
        }

        @Override
        public void userLikedImage() {
            likeimageView.setImageDrawable(context.getResources().getDrawable(R.drawable.unlike_image));
        }

        @Override
        public void fetchImageLikes(int totalLikeCount) {
            imageLikesTextView.setText(String.valueOf(totalLikeCount));
            imageDTO.setNoOfLikes(totalLikeCount);
        }

        @Override
        public void errorGettingLise() {
            imageLikesTextView.setText("0");
        }
    }
}
