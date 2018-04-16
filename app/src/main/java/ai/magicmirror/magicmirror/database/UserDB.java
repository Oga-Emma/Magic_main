package ai.magicmirror.magicmirror.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.dto.UserDTO;
import ai.magicmirror.magicmirror.features.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.features.profile_setup.DreaProfileSetupActivity;
import ai.magicmirror.magicmirror.features.user_auth.SignInActivity;
import ai.magicmirror.magicmirror.features.user_auth.UserDAO;
import es.dmoral.toasty.Toasty;

import static ai.magicmirror.magicmirror.utils.FirebaseUtils.Database._USERS_TABLE;

public class UserDB {
    private static UserDB userDB;
    private final FirebaseUser currentUser;
    private final DatabaseReference myRef;
    private final FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;

    public static UserDB getInstance(Context context){
        if(userDB == null){
            userDB = new UserDB(context);
        }

        return userDB;
    }

    public UserDB(Context context) {
        this.context = context;


        // [START initialize_auth]

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]
    }

    public void getCurrentUserProfile(UserQueryReturn userQueryReturn){

        if(null != currentUser){

            myRef.child(_USERS_TABLE).child(currentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

//                                Toasty.normal(context, "Data exists", Toast.LENGTH_LONG).show();
                                userQueryReturn.onUserReturned(dataSnapshot.getValue(UserDTO.class));

                            }else{
                                userQueryReturn.onUserReturned(null);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            userQueryReturn.onUserReturned(null);
                        }
                    });
        }

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

    }

    public void signout(Activity activity){
        FirebaseAuth.getInstance().signOut();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient
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

    public interface UserQueryReturn{
        void onUserReturned(UserDTO user);
    }
}
