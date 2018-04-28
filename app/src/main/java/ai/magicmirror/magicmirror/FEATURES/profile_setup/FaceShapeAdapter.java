package ai.magicmirror.magicmirror.FEATURES.profile_setup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ai.magicmirror.magicmirror.R;

public class FaceShapeAdapter extends
        RecyclerView.Adapter<FaceShapeAdapter.FaceShapeHolder>{

    private String[] faceShape;
    private Context context;
    private OnFaceShapeSelected faceShapeSelected;
    private int itemIndex = -1;


    public FaceShapeAdapter(String[] faceShape, OnFaceShapeSelected onFaceShapeSelected) {
        this.faceShape = faceShape;

        faceShapeSelected = onFaceShapeSelected;
    }

    @NonNull
    @Override
    public FaceShapeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_faceshape, parent, false);

        this.context = parent.getContext();

        return new FaceShapeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaceShapeHolder holder, int position) {

        holder.itemView.setOnClickListener(view -> {
            itemIndex = position;
            notifyDataSetChanged();

            faceShapeSelected.onFaceShapeSelected(faceShape[position]);
        });

        if(itemIndex == position) {
            holder.itemView.setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.recycler_view_item_clicked_border));
        }else {
            holder.itemView.setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.recycler_view_item_clicked_default_border));
        }

        holder.faceShapeLabel.setText(faceShape[position].toUpperCase());
        holder.faceShapePreview.setImageResource(ProfileSetupUtils.getFaceShape(faceShape[position], context));
    }

    @Override
    public int getItemCount() {
        return faceShape.length;
    }

    class FaceShapeHolder extends RecyclerView.ViewHolder{

        private View itemView;

        private ImageView faceShapePreview;
        private TextView faceShapeLabel;

        public FaceShapeHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.faceShapePreview = itemView.findViewById(R.id.face_shape_preview_image_preview);
            this.faceShapeLabel = itemView.findViewById(R.id.face_shape_label);
        }

    }


    public interface OnFaceShapeSelected{
        void onFaceShapeSelected(String faceShape);
    }
}
