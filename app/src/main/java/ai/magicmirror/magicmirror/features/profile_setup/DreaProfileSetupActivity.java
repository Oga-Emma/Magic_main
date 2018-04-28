package ai.magicmirror.magicmirror.features.profile_setup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UserDB;
import ai.magicmirror.magicmirror.dto.UserDTO;
import ai.magicmirror.magicmirror.features.BaseActivity;
import ai.magicmirror.magicmirror.features.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.features.user_auth.SignInActivity;
import ai.magicmirror.magicmirror.utils.DialogUtils;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;
import ai.magicmirror.magicmirror.utils.ImagePickerUtils;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

public class DreaProfileSetupActivity extends BaseActivity
        implements FaceShapeAdapter.OnFaceShapeSelected,
        ComplexionAdapter.OnComlexionSelected, View.OnClickListener, UserDB.UserSignout {

    private static final int PICK_IMAGE_ID = 200;
    private static final String FADE_IN_ANIMATION = "fade_in";

    private static final long TIME_INTERVAL = 2000;
    private static final String TAG = DreaProfileSetupActivity.class.getSimpleName();
    RecyclerView faceShapeRecyclerView, complextionRecyclerView;
    Bitmap selfieImageFull = null;
    Bitmap selfieImageThumbnail = null;
    private long mBackPressed;
    private ScrollView scrollView;
    private LinearLayout introductionLayout, chooseUserNameLayout, uploadPictureLayout,
            chooseFaceShapeLayout, chooseComplexionLayout, creatingProfileLayout,
            profileCreatedLayout;
    private Button userNameDoneButton, selectImageButton, logoutDoneButton;
    private ImageView selfieImageView;
    private TextView swipeLeftOrRightTextView;
    private TextInputLayout usernameTextInputLayout;
    private EditText userNameEditText;
    private FaceShapeAdapter faceShapeAdapter;
    private Animation fadeInAnimation;
    private Handler handler;
    private boolean isRunning;
    private String selfieImageDownloadUrl;
    private Bitmap selfieImageBitmap = null;
    private AlertDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup_drea_activity);

        errorDialog = DialogUtils.getErrorDialogBuilder(this, "Connection failed",
                "No network connection\nPlease make sure you are connected to the internet")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .create();

        scrollView = findViewById(R.id.profile_drea_scroll_view);

        introductionLayout = findViewById(R.id.drea_introduction_layout);
        chooseUserNameLayout = findViewById(R.id.drea_choose_username_layout);
        uploadPictureLayout = findViewById(R.id.drea_choose_upload_picture_layout);
        chooseFaceShapeLayout = findViewById(R.id.drea_faceshape_layout);
        chooseComplexionLayout = findViewById(R.id.drea_choose_complextion_layout);
        creatingProfileLayout = findViewById(R.id.drea_creating_profile_layout);
        profileCreatedLayout = findViewById(R.id.drea_profile_created_layout);

        usernameTextInputLayout = findViewById(R.id.username_text_input_layout);
        userNameEditText = findViewById(R.id.user_name_edit_text);

        userNameDoneButton = findViewById(R.id.user_name_done_button);
        userNameDoneButton.setOnClickListener(this::onClick);
        logoutDoneButton = findViewById(R.id.logout_done_button);
        logoutDoneButton.setOnClickListener(this::onClick);

        selectImageButton = findViewById(R.id.select_selfie_image_button);
        selectImageButton.setOnClickListener(this);
        selfieImageView = findViewById(R.id.selfie_image_view);

        swipeLeftOrRightTextView = findViewById(R.id.swipe_left_or_right_text_view);

        faceShapeRecyclerView = findViewById(R.id.face_shape_recycler_view);
        faceShapeRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String[] faceShapes = {
                ProfileSetupUtils.DIAMOND,
                ProfileSetupUtils.OVAL,
                ProfileSetupUtils.ROUND,
                ProfileSetupUtils.SQUARE,
                ProfileSetupUtils.OBLONG,
                ProfileSetupUtils.HEART,
        };


        faceShapeAdapter = new FaceShapeAdapter(faceShapes, this);
        faceShapeRecyclerView.setAdapter(faceShapeAdapter);


        complextionRecyclerView = findViewById(R.id.face_complexion_recycler_view);
        complextionRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String[] complextion = {
                ProfileSetupUtils.COMPLEXTION_WHITE,
                ProfileSetupUtils.COMPLEXTION_BROWN,
                ProfileSetupUtils.COMPLEXTION_DARK};
        complextionRecyclerView.setAdapter(new ComplexionAdapter(complextion, this));


        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userNameDoneButton.setVisibility(View.VISIBLE);
            }
        });

        startSlides();
    }

    private void startSlides() {
        fadeIn(introductionLayout, 3000);
        fadeIn(chooseUserNameLayout, 7000);
        fadeIn(usernameTextInputLayout, 8500);
    }

    private void fadeIn(View view, long waitingDuration) {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after xms
                view.startAnimation(fadeInAnimation);
                view.setVisibility(View.VISIBLE);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 1000);
            }
        }, waitingDuration);
//        view.setVisibility(View.VISIBLE);
    }

    private void slideIn(View v) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.select_selfie_image_button) {
            Intent chooseImageIntent = ImagePickerUtils
                    .getPickImageIntent(DreaProfileSetupActivity.this);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

        } else if (view.getId() == R.id.user_name_done_button) {

            view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (!TextUtils.isEmpty(userNameEditText.getText().toString())) {
                fadeIn(uploadPictureLayout, 1000);
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after x-ms
                        selectImageButton.setVisibility(View.VISIBLE);
                    }
                }, 3000);

            } else {
                //show error

            }


        } else if (view.getId() == R.id.logout_done_button) {

            if (logoutDoneButton.getText().equals("Continue")) {
                setupUserProfile();

//                startActivity(new Intent(DreaProfileSetupActivity.this, FeedPageActivity.class));
//                finish();
            } else {

                UserDB.getInstance(this).signout(this, this);
            }
        }
    }

    private void setupUserProfile() {

        if (selfieImageView != null) {
//            Bitmap bitmap = selfieImageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            selfieImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

                    UploadTask ThumbnailUploadTask = thumbnail_image_path.putBytes(data);
                    ThumbnailUploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            String thumbnailImageUrl = downloadUrl.toString();

                            UserDTO user = new UserDTO();
                            user.setUserName(userNameEditText.getText().toString());
                            user.setProfileImagefullUrl(fullImageUrl);
                            user.setProfileImageThumbnailUrl(thumbnailImageUrl);
                            user.setFaceshape(1);
                            user.setFaceComplexion(1);

                            DatabaseReference database = FirebaseDatabase.getInstance()
                                    .getReference();
                            database.child(FirebaseUtils.Database._USERS_TABLE).child(userId).setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(DreaProfileSetupActivity.this,
                                                    FeedPageActivity.class));

                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.error(getApplicationContext(), "Error creating profile, please try agagin",
                                            Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });
                }
            });
        } else {
            Toasty.error(getApplicationContext(), "Error creating profile, please try agagin",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImageBitmap(ImageView imageView) {
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                selfieImageBitmap = ImagePickerUtils.getImageFromResult(this, resultCode, data);

                File file = ImagePickerUtils.convertBitmapToFile(this, selfieImageBitmap);

                if (file != null) {
                    try {
                        selfieImageFull = new Compressor(this)
                                .compressToBitmap(file);

                        selfieImageThumbnail = new Compressor(this)
                                .setMaxWidth(200)
                                .setMaxHeight(200)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToBitmap(file);

                        if (selfieImageFull != null && selfieImageThumbnail != null) {
                            selfieImageView.setImageBitmap(selfieImageFull);
                            selfieImageView.setVisibility(View.VISIBLE);

                            fadeIn(chooseFaceShapeLayout, 2000);
                            fadeIn(faceShapeRecyclerView, 3500);
                            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    swipeLeftOrRightTextView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        } else {
                            Toasty.error(this, "Error reading image, please try again",
                                    Toast.LENGTH_LONG).show();

                        }

//                Toasty.normal(getApplicationContext(), "Image selected", Toast.LENGTH_SHORT).show();
                        break;

                    } catch (Exception e) {
                        e.printStackTrace();

                        Log.e("TAG", e.getMessage());
                        Toasty.error(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                        break;
                    }
                } else {
                    Toasty.error(this, "Error1 reading image, please try again", Toast.LENGTH_LONG).show();
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }

    }


    @Override
    public void onFaceShapeSelected(String faceShape) {
//        Toast.makeText(this, faceShape, Toast.LENGTH_LONG).show();

        fadeIn(chooseComplexionLayout, 2000);
        fadeIn(complextionRecyclerView, 4000);
    }

    @Override
    public void onComplexionSelected(String complexion) {

        fadeIn(creatingProfileLayout, 2000);
        fadeIn(profileCreatedLayout, 6000);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logoutDoneButton.setText("Continue");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

//        continueToFeedButton.setVisibility(View.VISIBLE);
//        fadeIn(continueToFeedButton, 7500);
    }


    @Override
    public void onBackPressed() {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
                /*Toast.makeText(getBaseContext(),
                    "Press back again to exit", Toast.LENGTH_SHORT).show(); */

            Toasty.warning(getBaseContext(),
                    "Press back again to exit.", Toast.LENGTH_SHORT, true).show();
        }
        mBackPressed = System.currentTimeMillis();

    }

    @Override
    public void onSignoutSuccess() {

        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(SignInActivity.ACTIVITY_STARTED_FROM_LAUNCHER, false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSignoutFailed() {
        Toasty.error(this, "Signout failed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void networkAvailable() {
        super.networkAvailable();
        Log.i(TAG, "Network is available");

        if (errorDialog.isShowing())
            errorDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        super.networkUnavailable();
        Log.i(TAG, "No network");

        errorDialog.show();
    }
}
