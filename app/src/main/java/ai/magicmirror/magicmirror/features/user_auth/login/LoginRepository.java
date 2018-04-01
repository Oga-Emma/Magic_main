package ai.magicmirror.magicmirror.features.user_auth.login;

import ai.magicmirror.magicmirror.models.UserDTO;

/**
 * Created by seven on 3/30/18.
 */

public class LoginRepository implements LoginMVP.Repository {
    @Override
    public UserDTO phoneNumberAuth(String phoneNumber) {
        return null;
    }

    @Override
    public UserDTO googleAuth() {
        return null;
    }
}
