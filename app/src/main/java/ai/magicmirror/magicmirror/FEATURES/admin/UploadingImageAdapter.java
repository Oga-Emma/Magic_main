package ai.magicmirror.magicmirror.FEATURES.admin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.magicmirror.magicmirror.R;

public class UploadingImageAdapter extends
        RecyclerView.Adapter<UploadingImageAdapter.UploadingImageHolder>{

    @NonNull
    @Override
    public UploadingImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uploading_image_recycler_view_item, parent, false);

        return new UploadingImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadingImageHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class UploadingImageHolder extends RecyclerView.ViewHolder{

        public UploadingImageHolder(View itemView) {
            super(itemView);
        }
    }
}
