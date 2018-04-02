package ai.magicmirror.magicmirror.features.profile_setup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ai.magicmirror.magicmirror.R;

public class ProfileSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup_activity);

        getSupportActionBar().setTitle(R.string.title_drea_activity);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_setup_fragment_container,
                        new ProfileSetupFragment()).commit();
    }
}
