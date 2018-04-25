package ai.magicmirror.magicmirror.DAOs;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ai.magicmirror.magicmirror.DTO.ImageLikesDTO;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;

public class ImageLikesDAO {


    private static ImageLikesDAO imageLikesDAO;
    private Context context;
    private DatabaseReference database;

    public ImageLikesDAO(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance().getReference();
    }


    public static ImageLikesDAO getInstance(Context context){
        if(null == imageLikesDAO)
            imageLikesDAO = new ImageLikesDAO(context);

        return imageLikesDAO;
    }

    public void getImageLikesCount(String imageId, ImageLikes imageLikes){
        //get total image likes

        database.child(FirebaseUtils.IMAGE_LIKES_LOCATION).child(imageId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            ImageLikesDTO imageLikeDTO = dataSnapshot.getValue(ImageLikesDTO.class);
                            if(null != imageLikeDTO){
                                imageLikes.fetchImageLikes(imageLikeDTO.getLikeCount());
                            }else{
                                imageLikes.fetchImageLikes(0);
                            }
//                            likes = dataSnapshot.getValue();
                        }else {
                            imageLikes.fetchImageLikes(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        imageLikes.errorGettingLise();
                    }
                });
    }

    public void likeImage(String imageId, String userId, ImageLikes imageLikes){
        //get total likes, increment by one and update
        //use transactions incase of data corruption

        database.child(FirebaseUtils.IMAGE_LIKES_LOCATION).child(imageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Set<String> likesSet = new HashSet<>();
                        List<String> likesList = new ArrayList<>();

                        ImageLikesDTO imageLikeDTO;

                        if(dataSnapshot.exists()){
                            imageLikeDTO = dataSnapshot.getValue(ImageLikesDTO.class);

//                            Log.i("TAG", "dataSnapshot exits");

                            if(null != imageLikeDTO){
                                likesSet = new HashSet<>(imageLikeDTO.getUserIds());

                                if(likesSet.contains(userId)){
//                                    Log.i("TAG", "disliked exits");
                                    likesSet.remove(userId);
                                    imageLikes.imageDisliked(imageLikeDTO.getLikeCount() - 1);
                                    imageLikeDTO.setLikeCount(imageLikeDTO.getLikeCount() - 1);

                                }else{
                                    likesSet.add(userId);
//                                    Log.i("TAG", "liked exits");
                                    imageLikes.imageLiked(imageLikeDTO.getLikeCount() + 1);
                                    imageLikeDTO.setLikeCount(imageLikeDTO.getLikeCount() + 1);
                                }

                                imageLikeDTO.setUserIds(new ArrayList<>(likesSet));

                            }else{
                                imageLikeDTO =
                                        new ImageLikesDTO(1, Arrays.asList(userId));

                                imageLikes.imageLiked(imageLikeDTO.getLikeCount());
                            }

                        }else{
                            Log.i("TAG", "datasnapshots does not exist");

                            imageLikeDTO =
                                    new ImageLikesDTO(1, Arrays.asList(userId));
                            imageLikes.imageLiked(imageLikeDTO.getLikeCount());
                        }

                        database.child(FirebaseUtils.IMAGE_LIKES_LOCATION)
                                .child(imageId)
                                .setValue(imageLikeDTO);


//                        Log.i("TAG", "LEAVING METHOD");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        imageLikes.errorGettingLise();
                    }
                });

    }

    public void userLikedImage(String imageId, String userId, ImageLikes imageLikes){
        //get total likes, increment by one and update
        //use transactions incase of data corruption

        database.child(FirebaseUtils.IMAGE_LIKES_LOCATION).child(imageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Set<String> likesSet = new HashSet<>();
                        List<String> likesList = new ArrayList<>();

                        ImageLikesDTO imageLikeDTO;

                        if(dataSnapshot.exists()){
                            imageLikeDTO = dataSnapshot.getValue(ImageLikesDTO.class);

//                            Log.i("TAG", "dataSnapshot exits");

                            if(null != imageLikeDTO){
                                likesSet = new HashSet<>(imageLikeDTO.getUserIds());

                                if(likesSet.contains(userId)){
//                                    Log.i("TAG", "disliked exits");
                                    imageLikes.userLikedImage();

                                }

                            }

                        }
//                        Log.i("TAG", "LEAVING METHOD");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }



    public interface ImageLikes {
        void imageLiked(int totalLikeCount);
        void imageDisliked(int totalLikeCount);
        void userLikedImage();
        void fetchImageLikes(int totalLikeCount);
        void errorGettingLise();
    }
}
