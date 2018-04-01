package ai.magicmirror.magicmirror.features.user_auth.login;

import ai.magicmirror.magicmirror.models.UserDTO;

/**
 * Created by seven on 3/30/18.
 */

public class LoginMVP {

    interface Model{
        void phoneNumberSignin(String phoneNumber);
    }

    interface View{
        void authenticatePhoneNumberSignIn(String phoneNumber);
        void authenticateEmailSignIn();
        void signInSuccess(UserDTO user);
        void signInFailed(String errorMessage);
        void showNoNetworkError();

    }

    interface Presenter{
        void phoneNumberSignIn(String phoneNumber);
        void googleSignIn();
    }

    interface Repository{
        UserDTO phoneNumberAuth(String phoneNumber);
        UserDTO googleAuth();
    }
}
