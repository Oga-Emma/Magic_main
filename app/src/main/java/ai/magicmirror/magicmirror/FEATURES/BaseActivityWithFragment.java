package ai.magicmirror.magicmirror.FEATURES;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import ai.magicmirror.magicmirror.R;

public class BaseActivityWithFragment extends AppCompatActivity{

//    abstract void setFragment(Fragment fragment);

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

}
