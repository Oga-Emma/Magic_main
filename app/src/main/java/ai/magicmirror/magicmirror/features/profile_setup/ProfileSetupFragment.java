package ai.magicmirror.magicmirror.features.profile_setup;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.feeds.FeedActivity;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;

import static ai.magicmirror.magicmirror.utils.FirebaseUtils.Strings.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSetupFragment extends Fragment {

private Button doneButton;

private FirebaseUser currentUser;
private DatabaseReference myRef;


    public ProfileSetupFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.profile_setup_fragment, container, false);

        doneButton = v.findViewById(R.id.profile_setup_fragment_done_button);
        doneButton.setOnClickListener(doneButtonPressed);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

        String key = myRef.child(USERS).getKey();
        if(!TextUtils.isEmpty(key)){
            Toast.makeText(getContext(), "user already has account",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "user does not have an account yet",
                    Toast.LENGTH_SHORT).show();
        }


        return v;

    }

    private View.OnClickListener doneButtonPressed
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Todo: 1 verify inputs are correct

            String id = currentUser.getUid();

            HashMap<String, String> userHashMap= new HashMap<>();
            userHashMap.put(USERNAME, "Mr7");
            userHashMap.put(FACESHAPE, "2");
            userHashMap.put(FACECOLOR, "1");
            userHashMap.put(PROFILE_PICTURE_FULL, "default");
            userHashMap.put(PROFILE_PICTURE_THUMBNAIL, "default");

            myRef.child(USERS)
                    .child(id)
                    .setValue(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(getContext(), FeedActivity.class));
                    }else{
                        Toast.makeText(getContext(),
                                "Error creating profile " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        Log.d("TAG", task.getException().getMessage());
                    }
                }
            });

           /* myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
//                    Log.d(TAG, "Value is: " + value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
//                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });*/

        }
    };

}
