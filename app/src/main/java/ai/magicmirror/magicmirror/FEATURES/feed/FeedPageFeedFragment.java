package ai.magicmirror.magicmirror.FEATURES.feed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ai.magicmirror.magicmirror.DAOs.OriginalImageDAO;
import ai.magicmirror.magicmirror.DTO.OriginalImageDTO;
import ai.magicmirror.magicmirror.DTO.UserDTO;
import ai.magicmirror.magicmirror.R;
import es.dmoral.toasty.Toasty;

public class FeedPageFeedFragment extends Fragment implements OriginalImageDAO.OnImageRetrieve{
    private static final String TAG = FeedPageFeedFragment.class.getSimpleName();
    public static final String SORTBY_PREF_KEY = "sortby_pref_key";

    public static final int SORTBY_SWAPS = 1;
    public static final int SORTYBY_UPLOADDATE = 2;
    public static final int SORTBY_LIKES = 3;


    private Toolbar toolbar;
    private Context mContext;

    private RecyclerView feedRecyclerView;
    private ProgressBar loadingProgress;

    private List<OriginalImageDTO> images;
    private FeedAdapter adapter;
    private UserDTO user;

    private FeedInterfaces.OnScrollChanged onScrollChanged;


    public FeedPageFeedFragment() {
        // Required empty public constructor
    }

    public static FeedPageFeedFragment newInstance() {
        FeedPageFeedFragment fragment = new FeedPageFeedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.feed_page_feed_fragment, container, false);

        loadingProgress = view.findViewById(R.id.loading_progress_bar);
        feedRecyclerView = view.findViewById(R.id.feed_recycler_view);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0){
                    onScrollChanged.onScroll(true);
//                    Log.i(TAG, "Scrolling up");
                }else{
                    onScrollChanged.onScroll(false);
//                    Log.i(TAG, "Scrolling down");
                }
            }
        });

        images = Collections.emptyList();
        adapter = new FeedAdapter(getActivity(), images);

        feedRecyclerView.setAdapter(adapter);
        feedRecyclerView.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.VISIBLE);

        OriginalImageDAO.getInstance(getContext()).getImages(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.user = ((FeedPageActivity) context).user;
        onScrollChanged = (FeedInterfaces.OnScrollChanged) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onScrollChanged = null;
    }

    @Override
    public void onImagesRetrieveSuccess(List<OriginalImageDTO> imageDTOList) {
        //TODO: sort image first when received

        images = imageDTOList;
        adapter = new FeedAdapter(getActivity(), images);

        feedRecyclerView.setAdapter(adapter);
        feedRecyclerView.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.GONE);

        Log.i(TAG, String.valueOf(imageDTOList.size()));
        Log.i(TAG, "URL: " + imageDTOList.get(0).getImageUrl());

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.feed_menu_action_sort).setVisible(false);
        menu.findItem(R.id.feed_menu_action_view).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onImageRetrieveFailed() {
        Toasty.warning(getContext(), "Error loading feeds", Toast.LENGTH_SHORT).show();

    }

    public void sortTypeChanged(){
        switch (PreferenceManager.getDefaultSharedPreferences(getContext())
                .getInt(SORTBY_PREF_KEY, SORTBY_SWAPS)){

            case SORTBY_SWAPS: Collections.shuffle(images);
                adapter.notifyDataSetChanged();break;

            case SORTYBY_UPLOADDATE: Collections.shuffle(images);
                adapter.notifyDataSetChanged();break;

            case SORTBY_LIKES: Collections.shuffle(images);
                adapter.notifyDataSetChanged();break;
        }
    }

}
