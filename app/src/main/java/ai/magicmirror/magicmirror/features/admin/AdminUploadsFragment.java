package ai.magicmirror.magicmirror.features.admin;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.magicmirror.magicmirror.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminUploadsFragment extends Fragment {


    public AdminUploadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_uploads_fragment, container, false);

        FloatingActionButton uploadButton
                 = view.findViewById(R.id.upload_floating_action_button);
//
//        Toolbar toolbar = view.findViewById(R.id.admin_upload_toolbar);
//        toolbar.setTitle("UPLOADS");
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UploadImageActivity.class));
            }
        });

        return view;
    }

}
