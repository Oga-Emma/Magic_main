package ai.magicmirror.magicmirror.features.profile_setup.profileSetup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
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

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UserDB;
import ai.magicmirror.magicmirror.dto.UserDTO;
import ai.magicmirror.magicmirror.features.BaseActivity;
import ai.magicmirror.magicmirror.features.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.features.profile_setup.ComplexionAdapter;
import ai.magicmirror.magicmirror.features.profile_setup.FaceShapeAdapter;
import ai.magicmirror.magicmirror.features.user_auth.SignInActivity;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;
import es.dmoral.toasty.Toasty;

public class ProfileSetupActivity extends BaseActivity implements View.OnClickListener,
        FaceShapeAdapter.OnFaceShapeSelected, ComplexionAdapter.OnComlexionSelected,
        Interface.SetUsernameAndSelfieImage {

    Button nextBtn;
    RadioButton radioButton1, radioButton2;
    Interface.OnImageSelectListener onImageSelectListener;
    UploadTask fullImagePploadTask;
    private String TAG = ProfileSetupActivity.class.getSimpleName();
    private String username;
    private Bitmap selfieImageFull;
    private Bitmap selfieImageThumbnail;
    private String faceShape;
    private String complexion;
    private ProgressDialog creatingAccountDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        nextBtn = findViewById(R.id.next_button);
        nextBtn.setOnClickListener(this);

        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);

        creatingAccountDialog = new ProgressDialog(this);
        creatingAccountDialog
                .setMessage("Creating account, please wait");
        creatingAccountDialog.setCancelable(false);
        creatingAccountDialog.setCanceledOnTouchOutside(false);
        creatingAccountDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(ProfileSetupActivity.this, SignInActivity.class));
                        finish();
                    }
                });

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left,
                        R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right)
                .replace(R.id.profile_setup_container, new UsernameProfilePicFragment())
                .commit();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == nextBtn.getId()) {
            if (nextBtn.getText().toString().equalsIgnoreCase("Next")) {

                if (selfieImageFull != null && !TextUtils.isEmpty(username)) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left,
                                    R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right)
                            .replace(R.id.profile_setup_container, new FaceshapeComplexionFragment())
                            .addToBackStack("FACE SHAPE")
                            .commit();

                    radioButton2.setChecked(true);
//                    ((RadioGroup)findViewById(R.id.radioGroup)).check(radioButton2.getId());
                    nextBtn.setText("Finish");
                } else {
                    if (TextUtils.isEmpty(username))
                        Toasty.warning(getApplicationContext(),
                                "you must type in a username", Toast.LENGTH_SHORT).show();

                    if (null == selfieImageFull)
                        Toasty.warning(getApplicationContext(),
                                "you must set your profile image, select a clear selfie image from galary " +
                                        "or snap one directly from using your camera", Toast.LENGTH_SHORT).show();
                }
            } else {

                if (!TextUtils.isEmpty(faceShape) && !TextUtils.isEmpty(complexion)) {

                    creatingAccountDialog.show();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    selfieImageFull.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference full_image_path = FirebaseStorage.getInstance().getReference()
                            .child(FirebaseUtils.USER_PROFILE_IMAGE__FULL_IMAGE_LOCATON)
                            .child(userId + "." + "jpg");

                    fullImagePploadTask = full_image_path.putBytes(data);
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

                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                            UserDTO user = new UserDTO();
                            user.setUserName(username);
                            user.setProfileImagefullUrl(fullImageUrl);
                            user.setFaceshape(1);
                            user.setFaceComplexion(1);

                            DatabaseReference database = FirebaseDatabase.getInstance()
                                    .getReference();
                            database.child(FirebaseUtils.Database._USERS_TABLE).child(userId).setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(ProfileSetupActivity.this, FeedPageActivity.class);
                                            intent.putExtra(SignInActivity.ACTIVITY_STARTED_FROM_LAUNCHER, true);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    creatingAccountDialog.dismiss();

                                    Toasty.error(getApplicationContext(), "there was an error creating your profile, " +
                                                    "please try agagin",
                                            Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });
                } else {

                    if (TextUtils.isEmpty(faceShape))
                        Toasty.warning(getApplicationContext(),
                                "select your face shape", Toast.LENGTH_SHORT).show();

                    if (TextUtils.isEmpty(complexion))
                        Toasty.warning(getApplicationContext(),
                                "select your complexion", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            nextBtn.setText("Next");
            radioButton1.setChecked(true);

//            ((RadioGroup)findViewById(R.id.radioGroup)).check(radioButton1.getId());
        } else {
            UserDB.getInstance(this).deleteUser(this);

            Intent intent = new Intent(this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onComplexionSelected(String complexion) {
        this.complexion = complexion;
        Log.i(TAG, "Complexion selected " + complexion);
    }

    @Override
    public void onFaceShapeSelected(String faceShape) {
        this.faceShape = faceShape;
        Log.i(TAG, "Faceshape selected " + faceShape);

    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setSelfieImage(Bitmap selfieImageFull, Bitmap selfieImageThumbnail) {
        this.selfieImageFull = selfieImageFull;
        this.selfieImageThumbnail = selfieImageThumbnail;
    }

}
