package ai.magicmirror.magicmirror.features.admin;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ai.magicmirror.magicmirror.R;
import es.dmoral.toasty.Toasty;

public class UploadImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_SELECTED_REQUEST_CODE = 500;
    private Button selectImageToUploadBotton;
    private ConstraintLayout uploadingImageLayout;
    private TextView uploadedImageTextView, uploadedImagecounterTextView, uploadedImagePercentTextView;

    private int uploadedCount;
    private List<Uri> failedToUpload;
    private List<Uri> filesToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image_activity);


        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)){
            Toasty.error(getApplicationContext(), "Sorry you need a device running on " +
                    "api 19[android Kitkat] and above to upload images").show();

            finish();
        }

        selectImageToUploadBotton = findViewById(R.id.select_image_to_upload_button);
        selectImageToUploadBotton.setOnClickListener(this);

        uploadingImageLayout = findViewById(R.id.uploading_image_layout);
        uploadedImageTextView = findViewById(R.id.image_file_names_text_view);
        uploadedImagecounterTextView = findViewById(R.id.uploaded_file_counter_text_view);
        uploadedImagePercentTextView = findViewById(R.id.uploaded_file_percent_text_view);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_SELECTED_REQUEST_CODE && resultCode == RESULT_OK){

            uploadingImageLayout.setVisibility(View.VISIBLE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(null != data.getClipData()){
                    //multiple images selected
//                    Toasty.info(getBaseContext(), "Multiple images selected").show();

                    filesToUpload = new ArrayList<>();

                    int totalCount = data.getClipData().getItemCount();
                    for (int i = 0; i < totalCount; i++) {
                        filesToUpload.add(data.getClipData().getItemAt(i).getUri());
                    }

                    uploadFilesToCloud(filesToUpload);

                }else if(null != data.getData()){
                    //single image selected
                    Toasty.info(getBaseContext(), "Single image selected").show();

                }
            }
        }
    }

    private void uploadFilesToCloud(List<Uri> fileUris) {
        uploadedCount = 0;

        StringBuilder fileName = new StringBuilder();

        int totalCount = fileUris.size();

        if(totalCount > 0) {
            for (int i = 0; i < totalCount; i++) {

                fileName.append(fileUris.get(i).toString()
                        .substring(fileUris.get(i).toString().lastIndexOf('/')));

                if (i > 1 && i != totalCount - 1)
                    fileName.append(", ");
            }

            if (!TextUtils.isEmpty(fileName.toString()))
                uploadedImageTextView.setText(fileName.toString());

            updateUploadingUi(uploadedCount, totalCount);

            //upload images
            for (Uri uri : fileUris ) {

                failedToUpload = new ArrayList<>(totalCount);

                if (!uploadImage(uri)) {
                    failedToUpload.add(uri);
                    continue;
                }

                //uploaded, update ui
                updateUploadingUi(++uploadedCount, totalCount);
            }

            if (failedToUpload.size() > 0) {
                //some files failed to upload
                //do something
            }
        }
    }

    private void updateUploadingUi(int uploadedCount, int totalCount) {
        uploadedImagecounterTextView.setText(getResources()
                .getString(R.string.uploaded_image_counter, uploadedCount, totalCount));

        uploadedImagePercentTextView.setText(getPercentageDone(uploadedCount, totalCount) + "%");
//        uploadedImagePercentTextView.setText(getResources()
//                .getString(R.string.uploaded_image_percent, getPercentageDone(uploadedCount, totalCount)));
    }

    private boolean uploadImage(Uri imageLocation) {
        //upload image

        return true;
    }

    private int getPercentageDone(int uploadedCount, int totalCount) {

        if(totalCount == 0)
            totalCount = 1;

        return (uploadedCount/totalCount) * 100;
    }

    private String getFileName(Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try{
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }

        if(result != null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut+1);
            }
        }

        return result;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.select_image_to_upload_button){
            Intent intent = new Intent();
            intent.setType("image/*");

            //Todo: make this work on older devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select images to upload"),
                    IMAGE_SELECTED_REQUEST_CODE);

        }
    }
}
