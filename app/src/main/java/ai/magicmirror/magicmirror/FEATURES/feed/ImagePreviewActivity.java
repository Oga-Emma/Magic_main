package ai.magicmirror.magicmirror.FEATURES.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import ai.magicmirror.magicmirror.DTO.ImageLikesDTO;
import ai.magicmirror.magicmirror.DTO.OriginalImageDTO;
import ai.magicmirror.magicmirror.FEATURES.user_profile.UserProfileActivity;
import ai.magicmirror.magicmirror.GlideApp;
import ai.magicmirror.magicmirror.R;

public class ImagePreviewActivity extends AppCompatActivity {

    private static final String IMAGE_INTENT = "image_intent";

    public static void launchActivity(Activity context, OriginalImageDTO imageDTO, final View imageView) {

        Intent profileIntent = new Intent(context, ImagePreviewActivity.class);
        profileIntent.putExtra(ImagePreviewActivity.IMAGE_INTENT, imageDTO);
// Pass data object in the bundle and populate details activity.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(context,
                            imageView, "preview_image");
            context.startActivity(profileIntent, options.toBundle());
        }else{
            context.startActivity(profileIntent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview_activity);

        ImageView imageView = findViewById(R.id.preview_image_view);
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(IMAGE_INTENT)){
            OriginalImageDTO imageDTO = getIntent().getExtras().getParcelable(IMAGE_INTENT);

            if(imageDTO != null){
                GlideApp.with(this)
                        .load(imageDTO.getImageUrl())
                        .placeholder(R.color.cardview_dark_background)
                        .into(imageView);
            }else{
                finish();
            }
        }else{
            finish();
        }


//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setFinishOnTouchOutside();
    }
}
