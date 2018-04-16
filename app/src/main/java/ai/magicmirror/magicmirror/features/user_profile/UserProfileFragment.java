package ai.magicmirror.magicmirror.features.user_profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.dto.UserDTO;

public class UserProfileFragment extends Fragment {

    private static final String USER_BUNDLE_KEY = "user_bundle_key";
    private UserDTO user = null;

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            user = savedInstanceState.getParcelable(USER_BUNDLE_KEY);
        }else if(getArguments() != null){
            user = getArguments().getParcelable(USER_BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.user_profile_fragment, container, false);
        ImageView profileImageView = view.findViewById(R.id.profile_image_image_view);
        Glide.with(getContext())
                .load(user.getProfileImagefullUrl())
                .into(profileImageView);

        return view;
    }

    public static UserProfileFragment getInstance(UserDTO user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_BUNDLE_KEY, user);

        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(USER_BUNDLE_KEY, user);
    }
}
