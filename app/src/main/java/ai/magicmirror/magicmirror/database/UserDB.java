package ai.magicmirror.magicmirror.database;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ai.magicmirror.magicmirror.FEATURES.profile_setup.profileSetup.ProfileSetupActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.DTO.UserDTO;

import static ai.magicmirror.magicmirror.utils.FirebaseUtils.Database._USERS_TABLE;

public class UserDB {
    private static UserDB userDB;
    private FirebaseUser currentUser;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

    }


    public void getCurrentUserProfile(GetUser getUser){

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(null != currentUser){

            myRef.child(_USERS_TABLE).child(currentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                UserDTO user = dataSnapshot.getValue(UserDTO.class);

                                if(null != user)
                                    getUser.onUserReturnedUser(user);
                                else{

                                    getUser.onNoUserRegistered();
                                }

                            }else{
                                getUser.onNoUserRegistered();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            getUser.onNoUserRegistered();
                        }
                    });
        }else{
            getUser.onNoUserRegistered();
        }

    }


    public void geUserAfterSigninProfile(GetUserAfterSignIn getUser){

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(null != currentUser){

            myRef.child(_USERS_TABLE).child(currentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                UserDTO user = dataSnapshot.getValue(UserDTO.class);

                                if(null != user)
                                    getUser.onUserAlreadyRegister(user);
                                else{

                                    getUser.onUserNotRegister();
                                }

                            }else{
                                getUser.onUserNotRegister();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            getUser.onUserNotRegister();
                        }
                    });
        }else{
            getUser.onUserRetrieveError();
        }

    }


    public void signInWithGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

    }

    public void signout(Activity activity, UserSignout userSignout){
        mAuth.signOut();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient
                = GoogleSignIn.getClient(context, gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google revoke access
                        mGoogleSignInClient.revokeAccess();
                        userSignout.onSignoutSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userSignout.onSignoutFailed();
            }
        });
    }

    public void signout(Activity activity){
        FirebaseAuth.getInstance().signOut();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient
                = GoogleSignIn.getClient(context, gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google revoke access
                        mGoogleSignInClient.revokeAccess();
                    }
                });
    }

    public void deleteUser(Context context) {
        mAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(context, gso).signOut();
        currentUser.delete();
    }

    public interface UserSignout {
        void onSignoutSuccess();
        void onSignoutFailed();
    }

    public interface GetUser {
        void onUserReturnedUser(UserDTO user);
        void onNoUserRegistered();
    }

    public interface GetUserAfterSignIn{
        void onUserAlreadyRegister(UserDTO user);
        void onUserNotRegister();
        void onUserRetrieveError();
    }
}
