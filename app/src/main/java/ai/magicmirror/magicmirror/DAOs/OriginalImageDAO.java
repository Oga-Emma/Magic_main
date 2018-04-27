package ai.magicmirror.magicmirror.DAOs;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ai.magicmirror.magicmirror.DTO.OriginalImageDTO;
import ai.magicmirror.magicmirror.DTO.UserDTO;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;

import static ai.magicmirror.magicmirror.utils.FirebaseUtils.Database._USERS_TABLE;

public class OriginalImageDAO {

    private static final String TAG = OriginalImageDAO.class.getSimpleName();

    private static OriginalImageDAO imageDAO;
    private Context context;
    private DatabaseReference database;


    public static OriginalImageDAO getInstance(Context context){
        if(imageDAO == null)
            imageDAO = new OriginalImageDAO(context);

        return imageDAO;
    }

    public OriginalImageDAO(Context context) {
        this.context = context;

        database = FirebaseDatabase.getInstance().getReference();
    }

    public void uploadImage(OriginalImageDTO imageDTO, OnImageUpload onImageUpload){
        database.child(FirebaseUtils.ORIGINAL_IMAGE_LOCATON)
                .child(imageDTO.getComplexion())
                .child(imageDTO.getFaceshape())
                .child(imageDTO.getImageId())
                .setValue(imageDTO);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onImageUpload.onImageUploadSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onImageUpload.onImageUploadFailed();
            }
        });
    }

    public void getImages(OnImageRetrieve onImageRetrieve){
        List<OriginalImageDTO> imageDTOList = new ArrayList<>();

        database.child(FirebaseUtils.ORIGINAL_IMAGE_LOCATON)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot complexionDirectory : dataSnapshot.getChildren())
                        if(complexionDirectory.exists()){

                            for(DataSnapshot faceshapeDirectory : complexionDirectory.getChildren()){
                                if(faceshapeDirectory.exists()){

                                    for(DataSnapshot image : faceshapeDirectory.getChildren()){
                                        OriginalImageDTO imageDTO = image.getValue(OriginalImageDTO.class);

                                        if(null != imageDTO){
                                                imageDTOList.add(imageDTO);
//                                                Log.i(TAG, imageDTO.toString());
                                            }
                                    }
                                }
                            }

                        }

                        onImageRetrieve.onImagesRetrieveSuccess(imageDTOList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onImageRetrieve.onImageRetrieveFailed();
                    }
                });

    }


    //////////////////////////////////
    public interface OnImageUpload{
        void onImageUploadSuccess();
        void onImageUploadFailed();
    }

    public interface OnImageRetrieve{
        void onImagesRetrieveSuccess(List<OriginalImageDTO> imageDTOList);
        void onImageRetrieveFailed();
    }
}
