package ai.magicmirror.magicmirror.FEATURES.user_auth.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import ai.magicmirror.magicmirror.FEATURES.profile_setup.profileSetup.ProfileSetupActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.DTO.UserDTO;
import ai.magicmirror.magicmirror.FEATURES.feed.FeedPageActivity;
import ai.magicmirror.magicmirror.FEATURES.user_auth.LoginVerifyPhoneNumberDialogFragment;
import ai.magicmirror.magicmirror.models.CountryDTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneNumberLoginFragment extends Fragment implements View.OnClickListener,
LoginMVP.View, LoginVerifyPhoneNumberDialogFragment.LoginVerifyPhoneNumber{

    private static final String TAG = "MagicMirror.ai " +
            PhoneNumberLoginFragment.class.getSimpleName();
    public static final int FRAGMENT_REQUEST_CODE = 1000;
    private static final int LOGIN_VERIFY_PASSWORD_REQUEST_CODE = 100;

    private Button signInBtn, googleSignInBtn;
    private EditText phoneNumberEdt, countryEdt;
    private TextView errorMessageTV;
    TextInputLayout countryTextInputLayout;

    LoginMVP.Presenter presenter;
    LoginMVP.View view;
    LoginMVP.Repository repository;

    private String phoneNumber;
    private int position = -1;

    public PhoneNumberLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.phone_number_login_fragment, container, false);

        repository = new LoginRepository();

        presenter = new LoginPresenter(this, repository);

        countryEdt = view.findViewById(R.id.login_country_edit_text);
        countryEdt.setOnClickListener(this);
        phoneNumberEdt = view.findViewById(R.id.login_activity_phone_number_edit_text);
        phoneNumberEdt.requestFocus();
        phoneNumberEdt.setOnClickListener(this);

        errorMessageTV = view.findViewById(R.id.login_activity_error_message_text_view);
        signInBtn = view.findViewById(R.id.login_activity_sign_in_button);

        signInBtn.setOnClickListener(this);


        CountryDTO country = new CountryDTO("Nigeria", "+234");
        countryEdt.setText(country.getName() + " (" + country.getCallCode() + ")");
        presenter.setCountry(country);

        return view;
    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.login_activity_sign_in_button){

            phoneNumber = phoneNumberEdt.getText().toString();
            presenter.phoneNumberSignIn(phoneNumber);

        }else if(v.getId() == R.id.login_country_edit_text){
            countryEdt.clearFocus();
            if(position == -1)
                position = 160;

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.login_activity_fragment_container, LoginSelectCountryFragment.newInstance(position))
                    .addToBackStack("SELECT_COUNTRY")
                    .commit();
        }else if(v.getId() == R.id.login_activity_phone_number_edit_text){
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(phoneNumberEdt, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    @Override
    public void authenticatePhoneNumberSignIn(String phoneNumber) {

        LoginVerifyPhoneNumberDialogFragment dialog
                = LoginVerifyPhoneNumberDialogFragment.getInstance(phoneNumber);
        dialog.setCancelable(false);

        dialog.setTargetFragment(this, LOGIN_VERIFY_PASSWORD_REQUEST_CODE);

        dialog.show(getActivity().getSupportFragmentManager(), "VERIFY_PHONE");
    }

    public void setCountry(CountryDTO country){
        if(null != country) {
            countryEdt.setText(country.getName() + " (" + country.getCallCode() + ")");
            presenter.setCountry(country);
        }
    }

    public void setCountryPosition(int position) {
        this.position = position;
    }

    @Override
    public void authenticateEmailSignIn() {

    }

    @Override
    public void signInSuccess(UserDTO user) {
        errorMessageTV.setText("");

        if(user == null) {
            startActivity(new Intent(getActivity(), ProfileSetupActivity.class));
        }else{
            startActivity(new Intent(getActivity(), FeedPageActivity.class));
        }

        getActivity().finish();

    }

    @Override
    public void signInFailed(String errorMessage) {
        errorMessageTV.setText(errorMessage);
        phoneNumberEdt.setText("");
        phoneNumberEdt.requestFocus();
    }

    @Override
    public void showNoNetworkError() {

    }

    @Override
    public void onLoginSuccessful(FirebaseUser user) {
        presenter.loginSuccessful(user);
    }

}
