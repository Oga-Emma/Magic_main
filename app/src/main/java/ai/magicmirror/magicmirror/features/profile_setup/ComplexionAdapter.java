package ai.magicmirror.magicmirror.features.profile_setup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import ai.magicmirror.magicmirror.R;

public class ComplexionAdapter extends
        RecyclerView.Adapter<ComplexionAdapter.ComplexionHolder> {

    String[] complexion;
    Context context;
    OnComlexionSelected comlexionSelected;
    private int itemIndex = -1;

    public ComplexionAdapter(String[] complexion, OnComlexionSelected onComlexionSelected) {

        comlexionSelected = onComlexionSelected;

        this.complexion = complexion;
    }

    @NonNull
    @Override
    public ComplexionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_complextion, parent, false);

        this.context = parent.getContext();
        return new ComplexionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplexionHolder holder, int position) {
//        holder.bindView(complexion[position]);


        holder.itemView.setOnClickListener(view -> {
            itemIndex = position;
            notifyDataSetChanged();

            comlexionSelected.onComplexionSelected(complexion[position]);
        });

        if (itemIndex == position) {
            holder.itemView.setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.recycler_view_item_clicked_border));
        } else {
            holder.itemView.setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.recycler_view_item_clicked_default_border));
        }

        holder.complexionLabel.setText(complexion[position].toUpperCase());
        holder.complexionPreview.setBackgroundDrawable(ProfileSetupUtils
                .getComplextionResourceImage(complexion[position], context));


    }

    @Override
    public int getItemCount() {
        return complexion.length;
    }

    public interface OnComlexionSelected {
        void onComplexionSelected(String complexion);
    }

    class ComplexionHolder extends RecyclerView.ViewHolder {
        private FrameLayout complexionPreview;
        private TextView complexionLabel;
        private View itemView;

        public ComplexionHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            complexionPreview = itemView.findViewById(R.id.complextion_color_frameLayout);
            complexionLabel = itemView.findViewById(R.id.complexion_image_label);
        }

    }

}
