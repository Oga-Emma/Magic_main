package ai.magicmirror.magicmirror.FEATURES.user_auth.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import ai.magicmirror.magicmirror.FEATURES.BaseActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.models.CountryDTO;
import ai.magicmirror.magicmirror.utils.DialogUtils;

public class PhoneNumberLoginActivity extends BaseActivity implements
        LoginSelectCountryFragment.OnCountrySelected{


    private static final String TAG = PhoneNumberLoginActivity.class.getSimpleName();
    private AlertDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_login_activity);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_activity_fragment_container,
                        new PhoneNumberLoginFragment(), "LOGIN FRAGMENT").commit();

        errorDialog = DialogUtils.getErrorDialogBuilder(this, "Connection failed",
                "No network connection\nPlease make sure you are connected to the internet")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .create();
    }

    @Override
    public void onCountrySelected(CountryDTO item, int position) {
        PhoneNumberLoginFragment fragment = (PhoneNumberLoginFragment)getSupportFragmentManager()
                .findFragmentByTag("LOGIN FRAGMENT");

        if(null != fragment) {
            fragment.setCountry(item);
            fragment.setCountryPosition(position);

            getSupportFragmentManager().popBackStack();

//            Toast.makeText(getApplicationContext(), position + " clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void networkAvailable() {
        super.networkAvailable();
        Log.i(TAG, "Network is available");

        if(errorDialog.isShowing())
            errorDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        super.networkUnavailable();
        Log.i(TAG, "No network");

        errorDialog.show();
    }
}
