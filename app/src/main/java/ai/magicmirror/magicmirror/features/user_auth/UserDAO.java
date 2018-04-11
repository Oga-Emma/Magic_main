package ai.magicmirror.magicmirror.features.user_auth;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ai.magicmirror.magicmirror.R;

public class UserDAO {
    private static UserDAO userDAO;

    private Context context;

    FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference myRef;
    private GoogleSignInClient mGoogleSignInClient;

    public UserDAO(Context context) {
        this.context = context;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
    }

    public static UserDAO getInstance(Context context){
        if(userDAO == null){
            userDAO = new UserDAO(context);
        }

        return userDAO;
    }

    public void signInWithGoogle(){
        //[GOOGLE SIGN IN]
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    public void signout(Activity activity){
        FirebaseAuth.getInstance().signOut();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        GoogleSignInClient mGoogleSignInClient
                = GoogleSignIn.getClient(context, gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
}
