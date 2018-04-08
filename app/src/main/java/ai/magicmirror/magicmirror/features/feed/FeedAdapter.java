package ai.magicmirror.magicmirror.features.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.swap.SwapActivity;
import ai.magicmirror.magicmirror.features.swap.SwapFragment;
import ai.magicmirror.magicmirror.features.user_profile.UserProfileFragment;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedHolder>{

    Context context;

    public FeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_fragment_recycler_view_item, parent, false);

        return new FeedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class FeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView feedImage, swapImageView;
        public FeedHolder(View itemView) {
            super(itemView);

            feedImage = itemView.findViewById(R.id.feed_image_view);
            swapImageView = itemView.findViewById(R.id.swap_image_view);

            swapImageView.setOnClickListener(this);
//            feedImage.setOnTouchListener(new ImageMatrixTouchHandler(itemView.getContext()));

//            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {

            view.getContext().startActivity(new Intent(view.getContext(), SwapActivity.class));
//            FragmentManager supportFragmentManager =
//                    ((AppCompatActivity) context).getSupportFragmentManager()
//                    .beginTransaction()
//                    .add();
            SwapFragment dialog = new SwapFragment();
        }
    }
}
