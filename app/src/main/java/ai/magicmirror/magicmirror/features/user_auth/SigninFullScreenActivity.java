package ai.magicmirror.magicmirror.features.user_auth;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ai.magicmirror.magicmirror.BaseActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.features.profile_setup.DreaProfileSetupActivity;
import ai.magicmirror.magicmirror.features.user_auth.login.PhoneNumberLoginActivity;

import static ai.magicmirror.magicmirror.utils.FirebaseUtils.Strings.USERS;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SigninFullScreenActivity extends BaseActivity {

    private FirebaseUser currentUser;
    private DatabaseReference myRef;

    ImageView appLogoImageView, captionImageView;

    ConstraintLayout signInButton;

    View spinKitView;

    View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_fullscreen_activity);
//        getSupportActionBar().hide();

        decorView = getWindow().getDecorView();
        spinKitView = findViewById(R.id.spin_kit);

        signInButton = findViewById(R.id.sign_in_button_layout);

        appLogoImageView = findViewById(R.id.logo_image_view);
        captionImageView = findViewById(R.id.magic_mirror_caption);

        Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.spalsh_screen_fade_in_animation);
        appLogoImageView.startAnimation(fade_in);
        captionImageView.startAnimation(fade_in);

        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                spinKitView.setVisibility(View.VISIBLE);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
//                    Toast.makeText(this, user.getUid() + " signed in", Toast.LENGTH_SHORT).show();

                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    myRef = FirebaseDatabase.getInstance().getReference();

                    myRef.child(USERS).child(currentUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                /*Toast.makeText(getApplicationContext(), "user already has account",
                                        Toast.LENGTH_SHORT).show();*/

                                        startActivity(new Intent(SigninFullScreenActivity.this,
                                                FeedPageActivity.class));

                                        finish();

                                    }else{
                                        startActivity(new Intent(SigninFullScreenActivity.this,
                                                DreaProfileSetupActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
//                                    Toast.makeText(getApplicationContext(), "error " +
//                                                    databaseError.getMessage(),
//                                            Toast.LENGTH_SHORT).show();

                                }
                            });

                }else{
                    spinKitView.setVisibility(View.INVISIBLE);
                    Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_animation);
                    slideIn.setDuration(1000);
                    signInButton.startAnimation(slideIn);
                    signInButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        findViewById(R.id.sign_in_with_phone_button)
                .setOnClickListener( view -> {
                    startActivity(new Intent(SigninFullScreenActivity.this,
                            PhoneNumberLoginActivity.class));
                });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }



}
