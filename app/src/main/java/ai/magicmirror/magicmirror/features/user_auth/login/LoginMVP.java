package ai.magicmirror.magicmirror.features.user_auth.login;

import com.google.firebase.auth.FirebaseUser;

import ai.magicmirror.magicmirror.dto.UserDTO;
import ai.magicmirror.magicmirror.models.CountryDTO;

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
        void loginSuccessful(FirebaseUser user);
        void setCountry(CountryDTO country);
    }

    interface Repository{
        UserDTO getUserInformation(FirebaseUser user);
    }
}
