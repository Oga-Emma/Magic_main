package ai.magicmirror.magicmirror.FEATURES.swap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;

import ai.magicmirror.magicmirror.DTO.OriginalImageDTO;
import ai.magicmirror.magicmirror.GlideApp;
import ai.magicmirror.magicmirror.R;

public class SwapActivity extends AppCompatActivity {

    public static final String SWAP_ACTIVITY_IMAGE_FROM_FEED_INTENT = "image_from_feed_intent";
    private static final String SWAP_ACTIVITY_BITMAP_EXTRA = "image_from_phone";

    private PhotoView image;

    private AlertDialog selectSwapPictureDialog;
//    ImageView image;
    private OriginalImageDTO imageDTO;

    public static void launchActivity(Context context, Uri bitmapUri){
        Intent intent = new Intent(context, SwapActivity.class);
        intent.putExtra(SWAP_ACTIVITY_BITMAP_EXTRA, bitmapUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swap_activity);

        image = findViewById(R.id.photo_view);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if(extras.containsKey(SWAP_ACTIVITY_BITMAP_EXTRA)){
                try {
                    Bitmap bitmap
                            = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                            ((Uri)extras.getParcelable(SWAP_ACTIVITY_BITMAP_EXTRA)));

                    GlideApp.with(this)
                            .load(bitmap)
                            .placeholder(R.color.cardview_dark_background)
                            .into(image);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(extras.containsKey(SWAP_ACTIVITY_IMAGE_FROM_FEED_INTENT)) {
                imageDTO = getIntent().getParcelableExtra(SWAP_ACTIVITY_IMAGE_FROM_FEED_INTENT);

                if(null == imageDTO)
                    finish();

                GlideApp.with(this)
                        .load(imageDTO.getImageUrl())
                        .placeholder(R.color.cardview_dark_background)
                        .into(image);
            }else{
                finish();
            }

        }else{
//            throw new Exception("no image passed");
            finish();
        }



        View view = LayoutInflater.from(this).inflate(R.layout.swap_use_profile_pic_or_choose_own_pic_dialog,
                null);
        selectSwapPictureDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();

        selectSwapPictureDialog.show();

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
