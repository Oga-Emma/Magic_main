package ai.magicmirror.magicmirror.FEATURES.feed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import ai.magicmirror.magicmirror.FEATURES.swap.SwapActivity;
import ai.magicmirror.magicmirror.GlideApp;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UserDB;
import ai.magicmirror.magicmirror.DTO.UserDTO;
import ai.magicmirror.magicmirror.FEATURES.user_auth.SignInActivity;
import ai.magicmirror.magicmirror.FEATURES.user_profile.UserProfileActivity;

public class FeedPageActivity extends AppCompatActivity
        implements UserDB.GetUser, UserProfileActivity.OnProfilePictureChanged,
        FeedInterfaces.OnScrollChanged, View.OnClickListener {

    private static final long TIME_INTERVAL = 2000;
    private static final String TAG = FeedPageActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    FloatingActionButton fab;

    private long mBackPressed;

    private ImageView userProfileImageView;
    public UserDTO user = null;

    private FeedPageDiscoverFragment discoverFragment;
    private FeedPageFeedFragment feedFragment;

    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;


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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::onClick);

        userProfileImageView = findViewById(R.id.user_profile_image);
        userProfileImageView.setOnClickListener(this::onClick);

        //Try to get the current user
        UserDB.getInstance(this).getCurrentUserProfile(this);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = pref.edit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_medium_thumbnail:
                prefEditor.putInt(FeedPageDiscoverFragment.THUMBNAIL_SHARED_PREFERENCE_KEY,
                        FeedPageDiscoverFragment.MEDIUM_THUMBNAIL).apply();
                return true;

            case R.id.action_view_small_thumbnail:
                prefEditor.putInt(FeedPageDiscoverFragment.THUMBNAIL_SHARED_PREFERENCE_KEY,
                        FeedPageDiscoverFragment.SMALL_THUMBNAIL).apply();
                return true;

            case R.id.action_sortby_swaps:
                prefEditor.putInt(FeedPageFeedFragment.SORTBY_PREF_KEY,
                        FeedPageFeedFragment.SORTBY_SWAPS).apply();
                return true;

            case R.id.action_sortby_most_recent_upload:
                prefEditor.putInt(FeedPageFeedFragment.SORTBY_PREF_KEY,
                        FeedPageFeedFragment.SORTYBY_UPLOADDATE).apply();
                return true;

            case R.id.action_sortby_likes:
                prefEditor.putInt(FeedPageFeedFragment.SORTBY_PREF_KEY,
                        FeedPageFeedFragment.SORTBY_LIKES).apply();
                return true;

            case R.id.action_help:
                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_about:
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_exit:
                Toast.makeText(this, "Exit clicked", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                SwapActivity.launchActivity(FeedPageActivity.this, resultUri);
                Log.i(TAG, resultUri.toString());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onUserReturnedUser(UserDTO user) {
        if (null != user) {

            this.user = user;

            Glide.with(this)
                    .load(user.getProfileImagefullUrl())
                    .thumbnail(1.0f)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(userProfileImageView);


            GlideApp.with(this)
                    .load(user.getProfileImagefullUrl())
                    .preload();

            Log.d(TAG, "user found");
        } else {
            UserDB.getInstance(this).signout(FeedPageActivity.this);
            Intent intent = new Intent(this, SignInActivity.class);
            intent.putExtra(SignInActivity.ACTIVITY_STARTED_FROM_LAUNCHER, false);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onNoUserRegistered() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(SignInActivity.ACTIVITY_STARTED_FROM_LAUNCHER, false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        Log.d(TAG, "user not found");
    }

    @Override
    public void profilePictureChanged(String newUrl) {
        Glide.with(this)
                .load(newUrl)
                .thumbnail(1.0f)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(userProfileImageView);
    }

    @Override
    public void onScroll(boolean scrollUp) {
        if (scrollUp) {
            fab.hide();
        } else {
            fab.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(FeedPageActivity.this);
        } else if (v.getId() == R.id.user_profile_image) {
            UserProfileActivity.LaunchActivity(FeedPageActivity.this, user,
                    userProfileImageView);
        }

    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        pref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pref.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    switch (key) {
                        case FeedPageDiscoverFragment.THUMBNAIL_SHARED_PREFERENCE_KEY:
                            discoverFragment.thumbnailSizeChanged();
                            break;

                        case FeedPageFeedFragment.SORTBY_PREF_KEY:
                            feedFragment.sortTypeChanged();
                            break;
                    }
                }
            };

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                discoverFragment = FeedPageDiscoverFragment.newInstance();
                return discoverFragment;
            } else {
                feedFragment = FeedPageFeedFragment.newInstance();
                return feedFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }


}
