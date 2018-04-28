package ai.magicmirror.magicmirror.FEATURES.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ai.magicmirror.magicmirror.FEATURES.BaseActivity;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.FEATURES.user_auth.SignInActivity;
import es.dmoral.toasty.Toasty;

import static ai.magicmirror.magicmirror.FEATURES.user_auth.SignInActivity.ACTIVITY_STARTED_FROM_LAUNCHER;

public class AdminActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTextMessage;

    private static final long TIME_INTERVAL = 2000;
    private long mBackPressed;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_users:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.admin_container, new AdminUsersFragment(),"USERS")
                            .commit();

//                    Toast.makeText(AdminActivity.this, "users", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.navigation_uploaded_photos:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.admin_container, new AdminUploadsFragment(),"UPLOADS")
                            .commit();


//                    Toast.makeText(AdminActivity.this, "uploads", Toast.LENGTH_SHORT).show();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//        Toolbar toolbar = findViewById(R.id.admin_toolbar);
//        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.admin_container, new AdminUsersFragment(),"USERS")
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//
        if(item.getItemId() == R.id.admin_menu_logout){
            Intent intent = new Intent(AdminActivity.this, SignInActivity.class);
            intent.putExtra(ACTIVITY_STARTED_FROM_LAUNCHER, false);
            startActivity(intent);

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

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

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

}
