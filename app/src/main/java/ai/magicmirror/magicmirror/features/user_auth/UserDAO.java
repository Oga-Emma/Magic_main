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



    public UserDAO(Context context) {
        this.context = context;

    }

    public static UserDAO getInstance(Context context){
        if(userDAO == null){
            userDAO = new UserDAO(context);
        }

        return userDAO;
    }


}
