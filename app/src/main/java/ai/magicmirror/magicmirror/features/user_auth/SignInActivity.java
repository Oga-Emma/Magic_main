package ai.magicmirror.magicmirror.features.user_auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ai.magicmirror.magicmirror.features.BaseActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.admin.AdminActivity;
import ai.magicmirror.magicmirror.features.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.features.profile_setup.DreaProfileSetupActivity;
import ai.magicmirror.magicmirror.features.user_auth.login.PhoneNumberLoginActivity;
import ai.magicmirror.magicmirror.utils.InternetUtils;
import es.dmoral.toasty.Toasty;

import static ai.magicmirror.magicmirror.utils.FirebaseUtils.Database._USERS_TABLE;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignInActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 1200;
    public static final String ACTIVITY_STARTED_FROM_LAUNCHER = "activity_was_started_from_launcher";
    private static final String TAG = SignInActivity.class.getSimpleName();
    private FirebaseUser currentUser;
    private DatabaseReference myRef;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    ImageView appLogoImageView, captionImageView;

    ConstraintLayout signInButtonLayout;

    View spinKitView;

    View decorView;

    AlertDialog.Builder errorDialog, googleSigninErrorDialog;
    private int appLogoClicked;

    MaterialDialog.Builder googleSignInErrordialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_activity);
//        getSupportActionBar().hide();

        decorView = getWindow().getDecorView();
        spinKitView = findViewById(R.id.spin_kit);

        signInButtonLayout = findViewById(R.id.sign_in_button_layout);

        appLogoImageView = findViewById(R.id.logo_image_view);
        captionImageView = findViewById(R.id.magic_mirror_caption);

        appLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((++appLogoClicked) == 10){
                    startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                    finish();
                }
            }
        });
        //[GOOGLE SIGN IN]
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        findViewById(R.id.sign_in_with_phone_button)
                .setOnClickListener( view -> {
                    startActivity(new Intent(SignInActivity.this,
                            PhoneNumberLoginActivity.class));
                });

        findViewById(R.id.sign_in_with_google_button)
                .setOnClickListener( view -> {
                    googleSignIn();
                });

        googleSigninErrorDialog = new AlertDialog.Builder(this);
        googleSigninErrorDialog.setMessage("Error signing" +
                "\nPlease check your network connection and try again")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        googleSignIn();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

        errorDialog = new AlertDialog.Builder(this);
        errorDialog.setMessage("Error connecting to the internet" +
                "\nPlease check your network connection and try again")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        spinKitView.setVisibility(View.VISIBLE);
                        dialogInterface.dismiss();

                        //network check
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false);


        if(getIntent().getBooleanExtra(ACTIVITY_STARTED_FROM_LAUNCHER, true)) {

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

                    updateUI(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }else{
            appLogoImageView.setVisibility(View.VISIBLE);
            captionImageView.setVisibility(View.VISIBLE);
            updateUI(false);
        }


        String errorMessage = "There was a problem signin with google, please check your internet connection, " +
                "and make sure atleast one google account" +
                "is signined in on the phone.\n\n(NB: if problem persists try other signin options)";

        googleSignInErrordialog = new MaterialDialog.Builder(this)
                .title("Google authentication failed")
                .content(errorMessage)
                .negativeText("Close");

    }


    private void updateUI(boolean animate) {

            spinKitView.setVisibility(View.GONE);
            if(animate) {
                Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_animation);
                slideIn.setDuration(1000);
                signInButtonLayout.startAnimation(slideIn);
            }
            signInButtonLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed" + e.getMessage(), e);

                googleSignInErrordialog.show();
                updateUI(false);

            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Google signin success");
                            startActivity(new Intent(SignInActivity.this, FeedPageActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            googleSignInErrordialog.show();
                            updateUI(false);
                        }

                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void googleSignIn() {

        if (!InternetUtils.isConnected(getApplicationContext())) {
            googleSigninErrorDialog.show();

        } else {

            spinKitView.setVisibility(View.VISIBLE);
            signInButtonLayout.setVisibility(View.GONE);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }
    // [END signin]

}
