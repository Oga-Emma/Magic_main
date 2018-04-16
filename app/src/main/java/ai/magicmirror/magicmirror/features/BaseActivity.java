package ai.magicmirror.magicmirror.features;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ai.magicmirror.magicmirror.R;
import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity{

    private static final long TIME_INTERVAL = 2000;
    private long mBackPressed;

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

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else {
                /*Toast.makeText(getBaseContext(),
                    "Press back again to exit", Toast.LENGTH_SHORT).show(); */

            Toasty.warning(getBaseContext(),
                    "Press back again to exit.", Toast.LENGTH_SHORT, true).show();
        }
        mBackPressed = System.currentTimeMillis();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
