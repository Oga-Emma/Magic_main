package ai.magicmirror.magicmirror.features.profile_setup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.utils.ImagePickerUtils;

public class DreaProfileSetupActivity extends AppCompatActivity
        implements FaceShapeAdapter.OnFaceShapeSelected,
        ComplexionAdapter.OnComlexionSelected, View.OnClickListener{

    private static final int PICK_IMAGE_ID = 200;
    private static final String FADE_IN_ANIMATION = "fade_in";

    ScrollView scrollView;

    LinearLayout introductionLayout, chooseUserNameLayout, uploadPictureLayout,
                chooseFaceShapeLayout,chooseComplexionLayout, creatingProfileLayout,
                profileCreatedLayout;

    Button userNameDoneButton, selectImageButton, continueToFeedButton;

    ImageView selfieImageView;

    TextInputLayout usernameTextInputLayout;
    EditText userNameEditText;

    RecyclerView faceShapeRecyclerView, complextionRecyclerView;

    FaceShapeAdapter faceShapeAdapter;

    Animation fadeInAnimation;

    Handler handler;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup_drea_activity);

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
        continueToFeedButton = findViewById(R.id.continue_to_feed_button);
        continueToFeedButton.setOnClickListener(this::onClick);

        selectImageButton = findViewById(R.id.select_selfie_image_button);
        selectImageButton.setOnClickListener(this);
        selfieImageView = findViewById(R.id.selfie_image_view);

        faceShapeRecyclerView = findViewById(R.id.face_shape_recycler_view);
        faceShapeRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String [] faceShapes = {
                DreaProfileSetupUtils.DIAMOND,
                DreaProfileSetupUtils.OVAL,
                DreaProfileSetupUtils.ROUND,
                DreaProfileSetupUtils.SQUARE,
                DreaProfileSetupUtils.OBLONG,
                DreaProfileSetupUtils.HEART,
        };


        faceShapeAdapter = new FaceShapeAdapter(faceShapes, this);
        faceShapeRecyclerView.setAdapter(faceShapeAdapter);


        complextionRecyclerView = findViewById(R.id.face_complexion_recycler_view);
        complextionRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String [] complextion  = {
                DreaProfileSetupUtils.COMPLEXTION_WHITE,
                DreaProfileSetupUtils.COMPLEXTION_BROWN,
                DreaProfileSetupUtils.COMPLEXTION_DARK};
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

    private void fadeIn(View view, long waitingDuration){

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

    private void slideIn(View v){

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.select_selfie_image_button){
            Intent chooseImageIntent = ImagePickerUtils.getPickImageIntent(DreaProfileSetupActivity.this);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

        }else if(view.getId() == R.id.user_name_done_button){

            view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if(!TextUtils.isEmpty(userNameEditText.getText().toString())) {
                fadeIn(uploadPictureLayout, 1000);
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after xms
                        selectImageButton.setVisibility(View.VISIBLE);
                    }
                }, 3000);

            }else{
                //show error

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePickerUtils.getImageFromResult(this, resultCode, data);
                selfieImageView.setImageBitmap(bitmap);
                selfieImageView.setVisibility(View.VISIBLE);

                fadeIn(chooseFaceShapeLayout, 2000);
                fadeIn(faceShapeRecyclerView, 3500);

                break;

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

//        continueToFeedButton.setVisibility(View.VISIBLE);
        fadeIn(continueToFeedButton, 7500);
    }
}
