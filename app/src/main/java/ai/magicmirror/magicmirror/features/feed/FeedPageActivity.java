package ai.magicmirror.magicmirror.features.feed;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UserDB;
import ai.magicmirror.magicmirror.dto.UserDTO;
import ai.magicmirror.magicmirror.features.BaseActivity;
import ai.magicmirror.magicmirror.features.profile_setup.DreaProfileSetupActivity;
import ai.magicmirror.magicmirror.features.user_auth.SignInActivity;
import ai.magicmirror.magicmirror.features.user_profile.UserProfileActivity;

public class FeedPageActivity extends BaseActivity implements UserDB.GetUser {

    private static final long TIME_INTERVAL = 2000;
    private static final String TAG = FeedPageActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private long mBackPressed;

    private ImageView userProfileImageView;
    private UserDTO user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_page_activity);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setTitle("Magic Feed");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.getTabAt(0).setCustomView(R.layout.tabitem_layout_feed);
        tabLayout.getTabAt(1).setCustomView(R.layout.tabitem_layout_discover);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        userProfileImageView = findViewById(R.id.user_profile_image);
        userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(FeedPageActivity.this, UserProfileActivity.class);
                profileIntent.putExtra(UserProfileActivity.USER_INTENT, user);
                startActivity(profileIntent);
            }
        });

        UserDB.getInstance(this).getCurrentUserProfile(this);

    }

  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * CALLED WHEN USER OBJECT IS RETURED FROM FIREBASE
     * @param user : object returned
     */
    @Override
    public void onUserReturnedUser(UserDTO user) {
        if(user != null){
            this.user = user;
            Glide.with(this)
                    .load(user.getProfileImagefullUrl());
            Glide.with(this)
                    .load(user.getProfileImageThumbnailUrl())
                    .into(userProfileImageView);

            Log.d(TAG, "user found");
        }else{
            Intent intent = new Intent(this, DreaProfileSetupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Log.d(TAG, "user authenitcated but no profile found");
        }
    }

    @Override
    public void onNoUserRegistered() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(SignInActivity.ACTIVITY_STARTED_FROM_LAUNCHER, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        Log.d(TAG, "user not found");
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 1){
                return FeedPageDiscoverFragment.newInstance("Feed", "Gele");
            }else{
                return FeedPageFeedFragment.newInstance("Discover", "Feed");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem() != 0){
            mViewPager.setCurrentItem(0, true);
        }else{
            super.onBackPressed();
        }

    }
}
