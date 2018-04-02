package ai.magicmirror.magicmirror.features.user_auth.login;

import com.google.firebase.auth.FirebaseUser;

import ai.magicmirror.magicmirror.models.UserDTO;

/**
 * Created by seven on 3/30/18.
 */

public class LoginRepository implements LoginMVP.Repository {

    @Override
    public UserDTO getUserInformation(FirebaseUser user) {
        return null;
    }
}
