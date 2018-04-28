package ai.magicmirror.magicmirror.features.user_auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
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
import com.google.firebase.database.DatabaseReference;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UserDB;
import ai.magicmirror.magicmirror.dto.UserDTO;
import ai.magicmirror.magicmirror.features.BaseActivity;
import ai.magicmirror.magicmirror.features.admin.AdminActivity;
import ai.magicmirror.magicmirror.features.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.features.profile_setup.profileSetup.ProfileSetupActivity;
import ai.magicmirror.magicmirror.features.user_auth.login.PhoneNumberLoginActivity;
import ai.magicmirror.magicmirror.utils.DialogUtils;
import es.dmoral.toasty.Toasty;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignInActivity extends BaseActivity implements UserDB.GetUser, UserDB.GetUserAfterSignIn {

    public static final String ACTIVITY_STARTED_FROM_LAUNCHER = "activity_was_started_from_launcher";
    private static final int RC_SIGN_IN = 1200;
    private static final String TAG = SignInActivity.class.getSimpleName();
    ImageView appLogoImageView, captionImageView;
    ConstraintLayout signInButtonLayout;
    View spinKitView;
    View decorView;
    AlertDialog.Builder googleSigninErrorDialog;
    MaterialDialog.Builder googleSignInErrordialog;
    private FirebaseUser currentUser;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int appLogoClicked;
    private AlertDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_activity);

        decorView = getWindow().getDecorView();
        spinKitView = findViewById(R.id.spin_kit);

        signInButtonLayout = findViewById(R.id.sign_in_button_layout);

        appLogoImageView = findViewById(R.id.logo_image_view);
        captionImageView = findViewById(R.id.magic_mirror_caption);

        appLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((++appLogoClicked) == 10) {
                    startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                    finish();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.sign_in_with_phone_button).setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this,
                    PhoneNumberLoginActivity.class));
        });

        findViewById(R.id.sign_in_with_google_button).setOnClickListener(view -> {
            spinKitView.setVisibility(View.VISIBLE);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        String errorMessage = "There was a problem signin with google, please check your internet connection, " +
                "and make sure atleast one google account" +
                "is signined in on the phone.\n\n(NB: if problem persists try other signin options)";

        googleSignInErrordialog = new MaterialDialog.Builder(this)
                .title("Google authentication failed")
                .content(errorMessage).negativeText("Close")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        spinKitView.setVisibility(View.GONE);
                    }
                });

        errorDialog = DialogUtils.getErrorDialogBuilder(this, "Connection failed",
                "No network connection\nPlease make sure you are connected to the internet")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false).create();

        appLogoImageView.setVisibility(View.GONE);
        captionImageView.setVisibility(View.GONE);
        signInButtonLayout.setVisibility(View.GONE);

//        UserDB.getInstance(this).getCurrentUserProfile(this);

        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            Intent intent = new Intent(SignInActivity.this, FeedPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else
            showUI();

//      Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(),
//      R.anim.spalsh_screen_fade_in_animation);


    }

    private void showUI() {
        appLogoImageView.setVisibility(View.VISIBLE);
        captionImageView.setVisibility(View.VISIBLE);
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

                            UserDB.getInstance(getApplicationContext())
                                    .geUserAfterSigninProfile(SignInActivity.this);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            googleSignInErrordialog.show();
                        }

                    }
                });
    }
    // [END auth_with_google]


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


    //[GET USER AFTER SIGNIN SUCCESSFULL]
    @Override
    public void onUserReturnedUser(UserDTO user) {

        if (null != user) {
            startActivity(new Intent(SignInActivity.this, FeedPageActivity.class));
            finish();
        } else {
            UserDB.getInstance(this).signout(this);
            showUI();
        }
    }

    @Override
    public void onNoUserRegistered() {
//        Toasty.warning(this, "USER ACCOUNT NOT CREATED YET", Toast.LENGTH_SHORT).show();
//        startActivity(new Intent(SignInActivity.this, ProfileSetupActivity.class));
//        finish();

        showUI();
    }

    @Override
    public void onUserAlreadyRegister(UserDTO user) {
        Intent intent = new Intent(SignInActivity.this, FeedPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserNotRegister() {
        startActivity(new Intent(SignInActivity.this, ProfileSetupActivity.class));
    }

    @Override
    public void onUserRetrieveError() {
        Toasty.error(this, "An error occured please retry login in",
                Toast.LENGTH_SHORT).show();
    }
}
