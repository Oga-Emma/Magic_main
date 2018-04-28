package ai.magicmirror.magicmirror.features;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.broadcast_receivers.NetworkStateReceiver;
import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private static final long TIME_INTERVAL = 2000;
    public boolean networkIsAvailabe;
    private long mBackPressed;
    private NetworkStateReceiver networkStateReceiver;
    private IntentFilter networkStateFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkStateReceiver = new NetworkStateReceiver(this);
        networkStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkStateFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.activity_slide_from_right, R.anim.activity_slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.activity_slide_from_left, R.anim.activity_slide_to_right);
    }

    @Override
    public void onBackPressed() {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
                /*Toast.makeText(getBaseContext(),
                    "Press back again to exit", Toast.LENGTH_SHORT).show(); */

            Toasty.warning(getBaseContext(),
                    "Press back again to exit.", Toast.LENGTH_SHORT, true).show();
        }
        mBackPressed = System.currentTimeMillis();

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(networkStateReceiver, networkStateFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkStateReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void networkAvailable() {
        this.networkIsAvailabe = true;
    }

    @Override
    public void networkUnavailable() {
        this.networkIsAvailabe = false;
    }
}
