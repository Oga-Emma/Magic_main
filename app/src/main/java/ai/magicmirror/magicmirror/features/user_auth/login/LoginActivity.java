package ai.magicmirror.magicmirror.features.user_auth.login;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.user_auth.dialogs.LoginVerifyPhoneNumberDialogFragment;
import ai.magicmirror.magicmirror.models.UserDTO;

public class LoginActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_activity_fragment_container,
                        new LoginFragment(), "LOGIN FRAGMENT").commit();
    }

}
