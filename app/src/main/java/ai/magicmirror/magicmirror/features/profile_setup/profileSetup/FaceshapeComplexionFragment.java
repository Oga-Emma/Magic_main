package ai.magicmirror.magicmirror.features.profile_setup.profileSetup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.profile_setup.ComplexionAdapter;
import ai.magicmirror.magicmirror.features.profile_setup.FaceShapeAdapter;
import ai.magicmirror.magicmirror.features.profile_setup.ProfileSetupUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaceshapeComplexionFragment extends Fragment {

    RecyclerView faceshapeRecyclerView, complexionRecyclerView;


    public FaceshapeComplexionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.pagesetup_fragment_faceshape_complexion, container,
                false);

        faceshapeRecyclerView = v.findViewById(R.id.faceshape_recycler_view);
        complexionRecyclerView = v.findViewById(R.id.complexion_recycler_view);

        faceshapeRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 3));

        String[] faceShapes = {
                ProfileSetupUtils.DIAMOND,
                ProfileSetupUtils.OVAL,
                ProfileSetupUtils.ROUND,
                ProfileSetupUtils.SQUARE,
                ProfileSetupUtils.OBLONG,
                ProfileSetupUtils.HEART,
        };

        FaceShapeAdapter faceShapeAdapter = new FaceShapeAdapter(faceShapes,
                (FaceShapeAdapter.OnFaceShapeSelected) getContext());
        faceshapeRecyclerView.setAdapter(faceShapeAdapter);

        complexionRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        String[] complextion = {
                ProfileSetupUtils.COMPLEXTION_WHITE,
                ProfileSetupUtils.COMPLEXTION_BROWN,
                ProfileSetupUtils.COMPLEXTION_DARK};
        complexionRecyclerView.setAdapter(new ComplexionAdapter(complextion,
                (ComplexionAdapter.OnComlexionSelected) getActivity()));


        return v;
    }

    /////////////////////////////

}
