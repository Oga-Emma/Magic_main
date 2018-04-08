package ai.magicmirror.magicmirror.features.profile_setup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ai.magicmirror.magicmirror.R;

public class ComplexionAdapter extends
        RecyclerView.Adapter<ComplexionAdapter.ComplexionHolder>{

    String [] complexion;
    Context context;
    private int itemIndex = -1;

    OnComlexionSelected comlexionSelected;

    public ComplexionAdapter(String[] complexion, Context context) {

        this.context = context;
        comlexionSelected = (OnComlexionSelected) context;

        this.complexion = complexion;
    }

    @NonNull
    @Override
    public ComplexionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_complextion, parent, false);

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

        if(itemIndex == position) {
            holder.itemView.setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.recycler_view_item_clicked_border));
        }else {
            holder.itemView.setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.recycler_view_item_clicked_default_border));
        }

        holder.complexionLabel.setText(complexion[position].toUpperCase());
        holder.complexionPreview.setBackgroundDrawable(DreaProfileSetupUtils
                .getComplextionResourceImage(complexion[position], context));


    }

    @Override
    public int getItemCount() {
        return complexion.length;
    }

    class ComplexionHolder extends RecyclerView.ViewHolder{
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

    interface OnComlexionSelected{
        void onComplexionSelected(String complexion);
    }

}
