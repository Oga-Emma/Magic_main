package ai.magicmirror.magicmirror.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import ai.magicmirror.magicmirror.utils.FirebaseUtils;

public class UploadImageDB {

    private static final String TAG = "Magic Mirror.ai "
            + UploadImageDB.class.getSimpleName();
    private StorageReference mStorageRef;
    private static UploadImageDB imageDB;


    public static UploadImageDB getInstance(){
        if(null == imageDB)
            imageDB = new UploadImageDB();

        return imageDB;
    }

    private UploadImageDB(){
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public String uploadImage(Uri imageUri){

        final String[] imageDownloadURL = new String[1];
//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

        String imageUniqueId = UUID.randomUUID().toString();
//        StorageReference riversRef = mStorageRef
//                .child(FirebaseUtils.ORIGINAL_IMAGE_LOCATON + "/" + imageUniqueId + getFileImageExtension(imageUri));
        StorageReference riversRef = mStorageRef
                .child(FirebaseUtils.ORIGINAL_IMAGE_LOCATON + "/" + imageUniqueId + getFileImageExtension(imageUri));

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        if (downloadUrl != null) {
                            imageDownloadURL[0] = downloadUrl.toString();
                        }else{
                            imageDownloadURL[0] = "";
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.e("TAG", "Error uploading image "
                                + exception.getMessage(), exception);

                        imageDownloadURL[0] = "";
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

//        Log.i(TAG, imageDownloadURL[0]);

        return imageDownloadURL[0];
    }

    private String getFileImageExtension(Uri imageUri) {
//        ContentResolver cR = getContentResoulver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(cR.getType(imageUri));

        return ".png";
    }
}
