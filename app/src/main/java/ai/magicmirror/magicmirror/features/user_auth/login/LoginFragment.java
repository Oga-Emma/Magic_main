package ai.magicmirror.magicmirror.features.user_auth.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.user_auth.dialogs.LoginVerifyPhoneNumberDialogFragment;
import ai.magicmirror.magicmirror.models.UserDTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener,
LoginMVP.View{

    private static final String TAG = "MagicMirror.ai " +
            LoginFragment.class.getSimpleName();
    public static final int FRAGMENT_REQUEST_CODE = 1000;

    private Button signInBtn, googleSignInBtn;
    private EditText phoneNumberEdt;
    private TextView errorMessageTV;

    LoginMVP.Presenter presenter;
    LoginMVP.View view;
    LoginMVP.Repository repository;

    private String phoneNumber;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        repository = new LoginRepository();

        presenter = new LoginPresenter(this, repository);

        phoneNumberEdt = view.findViewById(R.id.login_activity_phone_number_edit_text);
        phoneNumberEdt.requestFocus();

        errorMessageTV = view.findViewById(R.id.login_activity_error_message_text_view);

        signInBtn = view.findViewById(R.id.login_activity_sign_in_button);
        googleSignInBtn = view.findViewById(R.id.login_activity_google_sign_in_button);

        signInBtn.setOnClickListener(this);
        googleSignInBtn.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.login_activity_sign_in_button){

            phoneNumber = phoneNumberEdt.getText().toString();

//            Toast.makeText(getContext(),
//                    "Phone Login button pressed, PHONE NUMBER = "
//                            + phoneNumber,
//                    Toast.LENGTH_SHORT).show();

//            presenter.phoneNumberSignIn(phoneNumberEdt.getText().toString());

            presenter.phoneNumberSignIn(phoneNumber);

            /*if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) &&
                    (phoneNumber.length() > 10)){
                sendVerificationCode(phoneNumber);
            }else {

            }*/

        }else if(v.getId() == R.id.login_activity_google_sign_in_button){
//            Toast.makeText(getApplicationContext(),
//                    "E-mail Login button pressed",
//                    Toast.LENGTH_SHORT).show();

            presenter.googleSignIn();
        }

    }

    @Override
    public void authenticatePhoneNumberSignIn(String phoneNumber) {
//        Toast.makeText(getContext(),
//                    "Phone Login button pressed, PHONE NUMBER = "
//                            + phoneNumber,
//                    Toast.LENGTH_SHORT).show();

        Toast.makeText(getContext(), "Sending verification code to " + phoneNumber,
                Toast.LENGTH_LONG).show();

        LoginVerifyPhoneNumberDialogFragment dialog
                = LoginVerifyPhoneNumberDialogFragment.getInstance(phoneNumber);
        dialog.setCancelable(false);

        dialog.show(getActivity().getSupportFragmentManager(), "VERIFY_PHONE");
    }

    @Override
    public void authenticateEmailSignIn() {

    }

    @Override
    public void signInSuccess(UserDTO user) {
        errorMessageTV.setText("");


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
}
