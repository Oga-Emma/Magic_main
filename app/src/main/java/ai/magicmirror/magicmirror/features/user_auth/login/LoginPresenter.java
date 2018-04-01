package ai.magicmirror.magicmirror.features.user_auth.login;


import android.telephony.PhoneNumberUtils;

import ai.magicmirror.magicmirror.models.UserDTO;

/**
 * Created by seven on 3/30/18.
 */

public class LoginPresenter implements LoginMVP.Presenter {

    private String TAG = "magicmirror.ai " + LoginPresenter.class.getSimpleName();

    private LoginMVP.View view;
    private LoginMVP.Repository repository;

    public LoginPresenter(LoginMVP.View view, LoginMVP.Repository repository){
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void phoneNumberSignIn(String phoneNumber) {

        //validate phone number
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {

            view.authenticatePhoneNumberSignIn(phoneNumber);


        }else {
            view.signInFailed("Please enter a valid phone number");
        }
    }

    @Override
    public void googleSignIn() {
        UserDTO userDTO = repository.googleAuth();
        if (null != userDTO) {
            //perform necessary signin
            view.signInSuccess(userDTO);
        } else {
            view.signInFailed("Error signing in! please try again");
        }
    }
}
