package ai.magicmirror.magicmirror.database;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.admin.AdminUploadsFragment;

public class UploadingFilesDialog extends DialogFragment
implements AdminUploadsFragment.OnFileUploaded {

    public boolean uploadCancelled;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.admin_uploading_files_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//        dialog.setCancelable(false);

        dialog.setView(view);

        return dialog.create();
    }

    @Override
    public void incrementUpdateCount(String count) {
//        uploadingTextView.setText(count);
    }

}
