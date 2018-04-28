package ai.magicmirror.magicmirror.FEATURES.user_profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import ai.magicmirror.magicmirror.FEATURES.profile_setup.profileSetup.ProfileSetupActivity;
import ai.magicmirror.magicmirror.FEATURES.user_auth.SignInActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UserDB;
import ai.magicmirror.magicmirror.DTO.UserDTO;
import ai.magicmirror.magicmirror.FEATURES.BaseActivity;
import ai.magicmirror.magicmirror.FEATURES.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;
import ai.magicmirror.magicmirror.utils.ImagePickerUtils;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static ai.magicmirror.magicmirror.utils.ConstantsUtils.Integers.PICK_IMAGE_ID;

public class UserProfileActivity extends AppCompatActivity implements UserDB.UserSignout, View.OnClickListener{

    public static final String USER_INTENT = "user_intent";
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private UserDTO user = null;
    private ProgressDialog settingImageDialog;
    static OnProfilePictureChanged profilePictureChanged;

    ImageView profileImageView;

    public static void LaunchActivity(Activity context, UserDTO user, ImageView userProfileImageView){
        Intent profileIntent = new Intent(context,
                UserProfileActivity.class);
        profileIntent.putExtra(UserProfileActivity.USER_INTENT, user);

        profilePictureChanged = ((OnProfilePictureChanged) context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(context,
                            userProfileImageView, "profile");
            context.startActivity(profileIntent, options.toBundle());
        }else{
            context.startActivity(profileIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.user_profile_activity_toolbar); // Attaching the layout to the toolbar object
//        setSupportActionBar(toolbar);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle("Magic profile");

        if(savedInstanceState != null){
            user = savedInstanceState.getParcelable(USER_INTENT);
        } else if(getIntent() != null){
            user = getIntent().getParcelableExtra(USER_INTENT);
        }

        settingImageDialog = new ProgressDialog(this);
        settingImageDialog.setMessage("Setting profile picture, please wait...");

        profileImageView = findViewById(R.id.profile_setup_profile_image_image_view);
        findViewById(R.id.change_picture_image_view).setOnClickListener(this::onClick);

        Glide.with(this)
                .load(user.getProfileImagefullUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(profileImageView);

        ((TextView) findViewById(R.id.username_text_view)).setText(user.getUserName());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(USER_INTENT, user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.user_profile_edit_menu_item:
                Toast.makeText(getApplicationContext(), "Edit profile clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.user_profile_logout_menu_item:
//                Toast.makeText(getApplicationContext(), "Logout clicked", Toast.LENGTH_SHORT).show();
                UserDB.getInstance(this)
                        .signout(this, this);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public void onSignoutSuccess() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onSignoutFailed() {
        Toasty.error(this, "Error signing out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        Intent chooseImageIntent = ImagePickerUtils
                .getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE_ID:

                settingImageDialog.show();

                Bitmap selfieImageBitmap = ImagePickerUtils.getImageFromResult(this, resultCode, data);

                File file = ImagePickerUtils.convertBitmapToFile(this, selfieImageBitmap);

                if(file != null) {
                    try {
                        Bitmap selfieImageFull = new Compressor(this)
                                .compressToBitmap(file);

                        Bitmap selfieImageThumbnail = new Compressor(this)
                                .setMaxWidth(200)
                                .setMaxHeight(200)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToBitmap(file);

                        if(selfieImageFull != null) {
                            //do some shits
                            profileImageView.setImageBitmap(selfieImageFull);
                            changeProfilePicture(selfieImageFull, selfieImageThumbnail);

                        }else{
                            settingImageDialog.dismiss();
                            Toasty.error(this, "Error1 reading image, please try again",
                                    Toast.LENGTH_LONG).show();

                        }

//                Toasty.normal(getApplicationContext(), "Image selected", Toast.LENGTH_SHORT).show();
                        break;

                    } catch (Exception e) {
                        settingImageDialog.dismiss();
                        e.printStackTrace();

                        Log.e(TAG, e.getMessage(), e);
                        Toasty.error(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                        break;
                    }
                }else{
                    settingImageDialog.dismiss();
                    Toasty.error(this, "MagicMirror.ai encountered an error while " +
                            "reading image, please try again", Toast.LENGTH_LONG).show();
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    private void changeProfilePicture(Bitmap selfieImageFull, Bitmap selfieImageThumbnail) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        selfieImageFull.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference full_image_path = FirebaseStorage.getInstance().getReference()
                .child(FirebaseUtils.USER_PROFILE_IMAGE__FULL_IMAGE_LOCATON)
                .child(userId + "." + "jpg");

        UploadTask fullImagePploadTask = full_image_path.putBytes(data);
        fullImagePploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String fullImageUrl = downloadUrl.toString();

                StorageReference thumbnail_image_path = FirebaseStorage.getInstance().getReference()
                        .child(FirebaseUtils.USER_PROFILE_IMAGE__THUMBNAIL_LOCATON)
                        .child(userId + "." + "jpg");

                        user.setProfileImagefullUrl(fullImageUrl);

                        DatabaseReference database = FirebaseDatabase.getInstance()
                                .getReference();
                        database.child(FirebaseUtils.Database._USERS_TABLE).child(userId).setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        settingImageDialog.dismiss();
                                        profilePictureChanged.profilePictureChanged(fullImageUrl);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                settingImageDialog.dismiss();

                                Toasty.error(getApplicationContext(), "there was an error changing your profile picture, " +
                                                "please try agagin",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
    }

    public interface OnProfilePictureChanged{
        void profilePictureChanged(String newUrl);
    }
}
