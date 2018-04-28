package ai.magicmirror.magicmirror.FEATURES.profile_setup;

import android.os.Bundle;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.FEATURES.BaseActivity;

public class ProfileSetupActivity extends BaseActivity {

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
