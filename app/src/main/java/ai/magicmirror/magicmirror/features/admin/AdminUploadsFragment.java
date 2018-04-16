package ai.magicmirror.magicmirror.features.admin;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UploadingFilesDialog;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminUploadsFragment extends Fragment {


    private static final int IMAGE_SELECTED_REQUEST_CODE = 200;

    OnFileUploaded fileUploaded;
    UploadingFilesDialog dialog;

    private StorageReference mStorageRef;
    private List<Uri> filesToUpload;
    private ArrayList<Uri> failedToUpload;

    int totalUploadCount;
    int currentUpload;

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

        mStorageRef = FirebaseStorage.getInstance().getReference();
//
//        Toolbar toolbar = view.findViewById(R.id.admin_upload_toolbar);
//        toolbar.setTitle("UPLOADS");
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)) {
                    Toasty.error(getContext(), "Sorry you need a device running on " +
                            "api 19[android Kitkat] and above to upload images").show();

//                    finish();
                } else {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                2000);
                    } else {
                        startGallery();
                    }
                }
            }
        });

        return view;
    }

    private void startGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");

        //Todo: make this work on older devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select images to upload"),
                IMAGE_SELECTED_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_SELECTED_REQUEST_CODE && resultCode == RESULT_OK) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (null != data.getClipData()) {
                    //multiple images selected

                    failedToUpload = new ArrayList<>();

                    totalUploadCount = data.getClipData().getItemCount();

                    dialog = new UploadingFilesDialog();
                    dialog.setCancelable(false);
                    try {
                        fileUploaded = (OnFileUploaded) dialog;

                    }catch (Exception e){
                        //not implemented
                    }

                    dialog.show(getActivity().getSupportFragmentManager(), "UPLOADING_IMAGE");
//
//                    if(null != dialog)
//                        fileUploaded.incrementUpdateCount("0/"+ totalUploadCount);

                    for (int i = 0; i < totalUploadCount; i++) {
                        currentUpload = i+1;
                        uploadImage(data.getClipData().getItemAt(i).getUri());
                    }
//
//                    if(failedToUpload.size() > 0){
//                        //some files failed to upload.
//                        //do something
//
//                        Toasty.error(getContext(), filesToUpload.size() + " file(s) failed to upload",
//                                Toast.LENGTH_LONG).show();
//                    }else {
//                        Toasty.success(getContext(), " file(s) uploaded",
//                                Toast.LENGTH_LONG).show();
//                    }

                } else if (null != data.getData()) {
                    //single image selected

                }
            }
        }
    }

    ///////////////////////////IMAGE UPLOAD////////////////////////////////////
    public void uploadImage(Uri imageUri) {
//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

        try {
            Bitmap bitmap = new Compressor(getContext())
                    .compressToBitmap(new File(getPathn(imageUri)));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String imageUniqueId = UUID.randomUUID().toString();
            StorageReference imageUpload = mStorageRef
                    .child(FirebaseUtils.ORIGINAL_IMAGE_LOCATON).child(imageUniqueId + "." + getFileImageExtension(imageUri));
            UploadTask imageUploadTask = imageUpload.putBytes(data);

            imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

//                        if(null != fileUploaded)
//                            fileUploaded.incrementUpdateCount(currentUpload + "/" + totalUploadCount);

                    if(null != dialog && currentUpload == totalUploadCount) {
                        Toasty.success(getContext(), "All files uploaded successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG",  e.getMessage(), e);

                    failedToUpload.add(imageUri);

                    if(null != dialog && currentUpload == totalUploadCount) {
//                            Toasty.success(getContext(), "All files uploaded successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getFileImageExtension(Uri imageUri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(imageUri));
    }

    private String getPathn(Uri imageUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver()
                .query(imageUri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }


    ///////////////////INTERFACE FOR DISPLAYING UPLOADING FILE COUNT //////////////////////
    public interface OnFileUploaded{
        void incrementUpdateCount(String count);
    }

}
