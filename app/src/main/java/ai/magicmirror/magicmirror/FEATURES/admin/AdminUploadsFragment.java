package ai.magicmirror.magicmirror.FEATURES.admin;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ai.magicmirror.magicmirror.DAOs.OriginalImageDAO;
import ai.magicmirror.magicmirror.DTO.OriginalImageDTO;
import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.database.UploadingFilesDialog;
import ai.magicmirror.magicmirror.utils.FaceUtils;
import ai.magicmirror.magicmirror.utils.FirebaseUtils;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminUploadsFragment extends Fragment
        implements OriginalImageDAO.OnImageUpload, OriginalImageDAO.OnImageRetrieve{


    private static final int IMAGE_SELECTED_REQUEST_CODE = 200;
    private static String TAG = AdminUploadsFragment.class.getSimpleName();

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_uploads_fragment, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        OriginalImageDAO.getInstance(getContext()).getImages(this);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.admin_upload_image) {
            startGallery();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.admin_sort_users).setVisible(false);
    }


    @Override
    public void onResume() {
        super.onResume();

        ((AdminActivity) getActivity()).setActionBarTitle("Hair models");
    }

    private void startGallery() {

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
                Intent intent = new Intent();
                intent.setType("image/*");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select images to upload"),
                        IMAGE_SELECTED_REQUEST_CODE);
                }
            }
        }

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

                    for (int i = 0; i < totalUploadCount; i++) {
                        currentUpload = i+1;
                        uploadImage(data.getClipData().getItemAt(i).getUri());
                    }


                } else if (null != data.getData()) {
                    //single image selected

                }
            }
        }
    }

    ///////////////////////////IMAGE UPLOAD////////////////////////////////////
    public void uploadImage(Uri imageUri) {

        String imageName = getFileName(imageUri);

        Log.i(TAG, imageName);
        try {
            Bitmap bitmap = new Compressor(getContext())
                    .compressToBitmap(new File(getPathn(imageUri)));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

//            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String imageUniqueId = UUID.randomUUID().toString();
            StorageReference imageUpload = mStorageRef
                    .child(FirebaseUtils.ORIGINAL_IMAGE_LOCATON)
                    .child(imageUniqueId + "." + getFileImageExtension(imageUri));

            UploadTask imageUploadTask = imageUpload.putBytes(data);

            imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    OriginalImageDTO image = new OriginalImageDTO();
                    image.setImageId(imageUniqueId);
                    image.setFaceshape(getFaceShape(imageName));
                    image.setComplexion(getComplexion(imageName));
                    image.setImageUrl(downloadUrl.toString());
                    image.setHairStyle(getHairStyle(imageName));

                    OriginalImageDAO.getInstance(getContext())
                            .uploadImage(image, AdminUploadsFragment.this);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG",  e.getMessage(), e);

                    failedToUpload.add(imageUri);
/*
                    if(null != dialog && currentUpload == totalUploadCount) {
//                            Toasty.success(getContext(), "All files uploaded successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }*/
                }
            });

        } catch (IOException e) {
            e.printStackTrace();

            Log.e(TAG, e.getMessage(), e);
            Toasty.error(getContext(), "Error uploading images, please try again",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private String getHairStyle(String imageName) {
        String[] split = imageName.split("_");

        return split[split.length-1];
    }

    private String getComplexion(String imageName) {
        if(imageName.contains("dar"))
            return FaceUtils.Complexion.COMPLEXTION_DARK;
        else if(imageName.contains("bro"))
            return FaceUtils.Complexion.COMPLEXTION_BROWN;
        else if(imageName.contains("cho"))
            return FaceUtils.Complexion.COMPLEXTION_WHITE;
        else return FaceUtils.Complexion.UKNOWN;

    }

    private String getFaceShape(String imageName) {
        if(imageName.contains("dia"))
            return FaceUtils.Faceshapes.DIAMOND;
        else if(imageName.contains("hea"))
            return FaceUtils.Faceshapes.HEART;
        else if(imageName.contains("obl"))
            return FaceUtils.Faceshapes.OBLONG;
        else if(imageName.contains("ov"))
            return FaceUtils.Faceshapes.OVAL;
        else if(imageName.contains("rou"))
            return FaceUtils.Faceshapes.ROUND;
        else if(imageName.contains("sq"))
            return FaceUtils.Faceshapes.SQUARE;
        else
            return FaceUtils.Faceshapes.UNKNOWN;
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

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onImageUploadSuccess() {


        if(null != dialog && currentUpload == totalUploadCount) {
//                            Toasty.success(getContext(), "All files uploaded successfully", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    @Override
    public void onImageUploadFailed() {

        if(null != dialog && currentUpload == totalUploadCount) {
//                            Toasty.success(getContext(), "All files uploaded successfully", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    @Override
    public void onImagesRetrieveSuccess(List<OriginalImageDTO> imageDTOList) {
        Log.i(TAG, String.valueOf(imageDTOList.size()));
    }

    @Override
    public void onImageRetrieveFailed() {
        Log.i(TAG, "ERROR FETCHING IMAGES");
    }

    ///////////////////INTERFACE FOR DISPLAYING UPLOADING FILE COUNT //////////////////////
    public interface OnFileUploaded{
        void incrementUpdateCount(String count);
    }

}
