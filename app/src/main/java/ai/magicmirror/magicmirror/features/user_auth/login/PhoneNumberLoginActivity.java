package ai.magicmirror.magicmirror.features.user_auth.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ai.magicmirror.magicmirror.BaseActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.models.CountryDTO;

public class PhoneNumberLoginActivity extends BaseActivity implements LoginSelectCountryFragment.OnCountrySelected{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_login_activity);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_activity_fragment_container,
                        new PhoneNumberLoginFragment(), "LOGIN FRAGMENT").commit();
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
}
