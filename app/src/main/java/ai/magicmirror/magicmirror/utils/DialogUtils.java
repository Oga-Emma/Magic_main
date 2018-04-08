package ai.magicmirror.magicmirror.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import ai.magicmirror.magicmirror.R;

public class DialogUtils {

    public static AlertDialog getProgressDialog(Context context, String message){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress_layout, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setView(view);

        return dialog.create();
    }
}
