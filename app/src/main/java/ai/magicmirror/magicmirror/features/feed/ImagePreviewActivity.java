package ai.magicmirror.magicmirror.features.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ai.magicmirror.magicmirror.GlideApp;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.dao.ImageLikesDAO;
import ai.magicmirror.magicmirror.dto.OriginalImageDTO;
import ai.magicmirror.magicmirror.features.swap.SwapActivity;

public class ImagePreviewActivity extends AppCompatActivity implements View.OnClickListener, ImageLikesDAO.ImageLikes {

    private static final String IMAGE_INTENT = "image_intent";

    ImageView swapImageView, likeimageView;
    TextView imageLikesTextView, imageSwapsTextView;

    private OriginalImageDTO imageDTO;
    private String userId;

    public static void launchActivity(Activity context, OriginalImageDTO imageDTO, final View imageView) {

        Intent profileIntent = new Intent(context, ImagePreviewActivity.class);
        profileIntent.putExtra(ImagePreviewActivity.IMAGE_INTENT, imageDTO);
// Pass data object in the bundle and populate details activity.

        context.startActivity(profileIntent);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(context,
                            imageView, "preview_image");
            context.startActivity(profileIntent, options.toBundle());
        } else {
            context.startActivity(profileIntent);
        }*/
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview_activity);

        ImageView imageView = findViewById(R.id.preview_image_view);
        swapImageView = findViewById(R.id.preview_swap_image_view);
        likeimageView = findViewById(R.id.preview_like_image_image_view);
        imageLikesTextView = findViewById(R.id.preview_image_likes_count_text_view);
        imageSwapsTextView = findViewById(R.id.preview_image_swap_count_text_view);

        likeimageView.setOnClickListener(this::onClick);
        swapImageView.setOnClickListener(this::onClick);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(enterTransition());
            getWindow().setSharedElementReturnTransition(returnTransition());
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(IMAGE_INTENT)) {
            imageDTO = getIntent().getExtras().getParcelable(IMAGE_INTENT);

            if (imageDTO != null) {
                this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                ImageLikesDAO.getInstance(this)
                        .getImageLikesCount(imageDTO.getImageId(), this);

                ImageLikesDAO.getInstance(this)
                        .userLikedImage(imageDTO.getImageId(), userId, this);

                GlideApp.with(this)
                        .load(imageDTO.getImageUrl())
                        .into(imageView);
            } else {
                finish();
            }
        } else {
            finish();
        }


//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setFinishOnTouchOutside();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.like_image_image_view) {
            ImageLikesDAO.getInstance(ImagePreviewActivity.this)
                    .likeImage(imageDTO.getImageId(), userId, this);

        } else {
            Intent intent = new Intent(view.getContext(), SwapActivity.class);
            intent.putExtra(SwapActivity.SWAP_ACTIVITY_IMAGE_FROM_FEED_INTENT, imageDTO);

            view.getContext().startActivity(intent);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(100);

        return bounds;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition returnTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new DecelerateInterpolator());
        bounds.setDuration(300);

        return bounds;
    }

    @Override
    public void imageLiked(int totalLikeCount) {
        imageLikesTextView.setText(String.valueOf(totalLikeCount));
        likeimageView.setImageDrawable(this.getResources().getDrawable(R.drawable.like_image));

    }

    @Override
    public void imageDisliked(int totalLikeCount) {
        imageLikesTextView.setText(String.valueOf(totalLikeCount));
        likeimageView.setImageDrawable(this.getResources().getDrawable(R.drawable.unlike_image));
    }

    @Override
    public void userLikedImage() {
        likeimageView.setImageDrawable(this.getResources().getDrawable(R.drawable.unlike_image));
    }

    @Override
    public void fetchImageLikes(int totalLikeCount) {
        imageLikesTextView.setText(String.valueOf(totalLikeCount));
    }

    @Override
    public void errorGettingLise() {
        imageLikesTextView.setText("0");
    }
}
