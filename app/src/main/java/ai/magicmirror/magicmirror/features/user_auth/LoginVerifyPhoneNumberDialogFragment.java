package ai.magicmirror.magicmirror.features.user_auth;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by seven on 3/31/18.
 */

public class LoginVerifyPhoneNumberDialogFragment extends DialogFragment {

    public static final String CREDENTIAL_BUNDLE_KEY = "credential";
    public static final String TAG = LoginVerifyPhoneNumberDialogFragment.class.getSimpleName();
    private static final String VERIFY_PHONE_FRAGMENT_PHONE_NUMBER_KEY = "phone_number";
    private static final String VERIFY_PHONE_FRAGMENT_RESEND_TOKEN_KEY = "resend_token";
    private static final String VERIFY_PHONE_FRAGMENT_CODE_SENT_KEY = "code_sent";
    private static final String VERIFY_PHONE_FRAGMENT_SHOW_ERROR_KEY = "show_error";
    Button neutralButton;
    Button positiveButton;
    Button negativeButton;
    LoginVerifyPhoneNumber callBack;
    private String phoneNumber;
    private boolean showError;
    private boolean isVerifyingCode;
    private ConstraintLayout verifyPasswordLayout, errorLayout,
            dialogLayout;
    private TextInputEditText verifyCodeEditText;
    private TextView verificationErrorTextView, progressMessageDialog;
    private PhoneAuthCredential credential;
    private FirebaseAuth auth;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//            Log.d(TAG, "onVerificationCompleted:" + credential);

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            showAuthErrorDialog("Error sending verification code. Please ensure you are " +
                    "connected to the internet and try again");

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
//            Log.d(TAG, "onCodeSent:" + verificationId);

            isVerifyingCode = false;
            showVerifyCodeDialog();

            Toast.makeText(getContext(), "Verification code sent", Toast.LENGTH_SHORT).show();
            // Save verification ID and resending token so we can use them later
            verificationCode = verificationId;
            mResendToken = token;


            // ...
        }
    };
    private AlertDialog progressDialog;


    public LoginVerifyPhoneNumberDialogFragment() {
    }

    public static LoginVerifyPhoneNumberDialogFragment getInstance(String phoneNumber) {
        LoginVerifyPhoneNumberDialogFragment dialog
                = new LoginVerifyPhoneNumberDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(VERIFY_PHONE_FRAGMENT_PHONE_NUMBER_KEY, phoneNumber);

        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (null != bundle) {

            try {
                callBack = (LoginVerifyPhoneNumber) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");


            }

            phoneNumber = bundle.getString(VERIFY_PHONE_FRAGMENT_PHONE_NUMBER_KEY, "");
            if (phoneNumber.isEmpty())
                showError = true;
            else {
                auth = FirebaseAuth.getInstance();
                auth.useAppLanguage();
                sendVerificationCode(phoneNumber);

            }
        } else {
            Toast.makeText(getContext(), "Could not verify phone number",
                    Toast.LENGTH_SHORT).show();
            dismiss();
        }

//        getDialog().setCanceledOnTouchOutside(false);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.user_auth_login_verify_password_dialog_fragment, null);

        verifyPasswordLayout = v.findViewById(R.id.auth_dialog_verify_code_constraint_layout);
        errorLayout = v.findViewById(R.id.auth_dialog_fragment_error_authenticating_constraint_layout);
        dialogLayout = v.findViewById(R.id.progress_dialog_layout);

        verifyCodeEditText = v.findViewById(R.id.verify_code_text_input_layout);
        verificationErrorTextView = v.findViewById(R.id.verification_error_text_view);
        progressMessageDialog = v.findViewById(R.id.dialog_text_text_view);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setView(v)
                .setCancelable(false);

        if (showError) {

            showAuthErrorDialog("Phone number authentication failed. \nPlease try again");
//            dialog.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
        } else {
            errorLayout.setVisibility(View.GONE);
            dialogLayout.setVisibility(View.GONE);
            verifyPasswordLayout.setVisibility(View.VISIBLE);
            dialog.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhoneAuthCredential credential = PhoneAuthProvider
                            .getCredential(verificationCode,
                                    verifyCodeEditText.getText().toString().trim());

                    signInWithPhoneAuthCredential(credential);
                }
            });

            dialog.setNeutralButton("Resend code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resendVerificationCode(phoneNumber, mResendToken);
                }
            });
        }

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return dialog.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!showError) {
            final AlertDialog d = (AlertDialog) getDialog();
            if (d != null) {
                neutralButton = d.getButton(Dialog.BUTTON_NEUTRAL);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //resend verification code
                        resendVerificationCode(phoneNumber, mResendToken);
                        showProgressDialogLayout("Resending verification code to " + phoneNumber);
                    }
                });


                positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Boolean wantToCloseDialog = false;
                        //Do stuff, possibly set wantToCloseDialog to true then...

                        showProgressDialogLayout("Verifying code...");

                        isVerifyingCode = true;

                        PhoneAuthCredential credential =
                                PhoneAuthProvider.getCredential(verificationCode,
                                        verifyCodeEditText.getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    }
                });


                negativeButton = d.getButton(Dialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isVerifyingCode) {
                            d.dismiss();
                        } else {
                            showVerifyCodeDialog();

                            isVerifyingCode = false;
                        }
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                    }
                });
            }

        }
        if (isVerifyingCode)
            showProgressDialogLayout("Sending verification code to " + phoneNumber);


        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    dialog.dismiss();
                    //Hide your keyboard here!!!
                    return true; // pretend we've processed it
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,               // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),      // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void sendVerificationCode(String phoneNumber) {


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                5,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        isVerifyingCode = true;

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                    final AlertDialog d = (AlertDialog) getDialog();

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verified successfully

                            FirebaseUser user = task.getResult().getUser();
                            callBack.onLoginSuccessful(user);
                            d.dismiss();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                            }

                            showAuthErrorDialog("Error authenticating phone number, check you" +
                                    "internet connection and try again");

//                            verificationErrorTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    public void showVerifyCodeDialog() {
        positiveButton.setVisibility(View.VISIBLE);
        neutralButton.setVisibility(View.VISIBLE);
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText("Cancel");

        verifyPasswordLayout.setVisibility(View.VISIBLE);
        dialogLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        isVerifyingCode = false;

    }

    public void showProgressDialogLayout(String message) {

        positiveButton.setVisibility(View.GONE);
        neutralButton.setVisibility(View.GONE);
        negativeButton.setVisibility(View.GONE);

        progressMessageDialog.setText(message);

        verifyPasswordLayout.setVisibility(View.GONE);
        dialogLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

    }

    private void showAuthErrorDialog(String message) {

        positiveButton.setVisibility(View.GONE);
        neutralButton.setVisibility(View.GONE);
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText("Close");

        verifyPasswordLayout.setVisibility(View.GONE);
        dialogLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);

        isVerifyingCode = false;
    }


    public interface LoginVerifyPhoneNumber {
        void onLoginSuccessful(FirebaseUser user);
    }

}
