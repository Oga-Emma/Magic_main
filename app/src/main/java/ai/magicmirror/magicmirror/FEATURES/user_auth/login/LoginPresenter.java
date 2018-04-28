package ai.magicmirror.magicmirror.FEATURES.user_auth.login;


import android.telephony.PhoneNumberUtils;

import com.google.firebase.auth.FirebaseUser;

import ai.magicmirror.magicmirror.DTO.UserDTO;
import ai.magicmirror.magicmirror.models.CountryDTO;

/**
 * Created by seven on 3/30/18.
 */

public class LoginPresenter implements LoginMVP.Presenter {

    private String TAG = "magicmirror.ai " + LoginPresenter.class.getSimpleName();

    private LoginMVP.View view;
    private LoginMVP.Repository repository;
    private CountryDTO country;

    public LoginPresenter(LoginMVP.View view, LoginMVP.Repository repository){
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void phoneNumberSignIn(String phoneNumber) {

        //validate phone number
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {

            if(phoneNumber.startsWith("0"))
                phoneNumber = phoneNumber.substring(1);

            phoneNumber = country.getCallCode() + phoneNumber;

            view.authenticatePhoneNumberSignIn(phoneNumber);
        }else {
            view.signInFailed("Please enter a valid phone number");
        }
    }

    @Override
    public void googleSignIn() {
    }

    @Override
    public void loginSuccessful(FirebaseUser user) {
        UserDTO userInfo = repository.getUserInformation(user);
        view.signInSuccess(userInfo);
    }

    @Override
    public void setCountry(CountryDTO country) {
        this.country = country;
    }
}
