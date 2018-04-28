package ai.magicmirror.magicmirror.features.admin;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import ai.magicmirror.magicmirror.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminUsersFragment extends Fragment {


    public AdminUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_users_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AdminActivity) getActivity()).setActionBarTitle("Users");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.admin_upload_image).setVisible(false);
        menu.findItem(R.id.admin_sort_by).setVisible(false);
    }

}
