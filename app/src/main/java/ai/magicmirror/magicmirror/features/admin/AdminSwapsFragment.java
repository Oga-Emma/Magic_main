package ai.magicmirror.magicmirror.features.admin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.magicmirror.magicmirror.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminSwapsFragment extends Fragment {


    public AdminSwapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_swaps_fragment, container, false);
    }

}
