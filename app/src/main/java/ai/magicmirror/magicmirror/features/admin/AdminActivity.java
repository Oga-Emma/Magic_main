package ai.magicmirror.magicmirror.features.admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ai.magicmirror.magicmirror.BaseActivity;
import ai.magicmirror.magicmirror.R;
import es.dmoral.toasty.Toasty;

public class AdminActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_users:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.admin_container, new AdminUsersFragment(),"USERS")
                            .commit();
                    return true;

                case R.id.navigation_uploaded_photos:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.admin_container, new AdminUploadsFragment(),"UPLOADS")
                            .commit();
                    return true;

                case R.id.navigation_swaps:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.admin_container, new AdminSwapsFragment(),"SWAPS")
                            .commit();
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

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if(item.getItemId() == R.id.admin_upload_menu_item){
//
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }


}
