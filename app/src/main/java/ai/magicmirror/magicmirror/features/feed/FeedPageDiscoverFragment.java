package ai.magicmirror.magicmirror.features.feed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.dao.OriginalImageDAO;
import ai.magicmirror.magicmirror.dto.OriginalImageDTO;
import es.dmoral.toasty.Toasty;

public class FeedPageDiscoverFragment extends Fragment implements OriginalImageDAO.OnImageRetrieve {

    public static final int MEDIUM_THUMBNAIL = 2;
    public static final int SMALL_THUMBNAIL = 3;
    public static final String THUMBNAIL_SHARED_PREFERENCE_KEY = "thumbnail_shared_pref";
    Toolbar toolbar;
    RecyclerView discoverRecyclerView;
    ProgressBar loadingProgress;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private CardView searchView;
    private FeedInterfaces.OnScrollChanged onScrollChanged;
    private GridLayoutManager gridLayoutManager;
    private List<OriginalImageDTO> imageDTOList;


    public FeedPageDiscoverFragment() {
        // Required empty public constructor

        setHasOptionsMenu(true);
    }

    public static FeedPageDiscoverFragment newInstance() {
        FeedPageDiscoverFragment fragment = new FeedPageDiscoverFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.feed_page_discover_fragment, container, false);

        List<OriginalImageDTO> list = Collections.emptyList();

        view.findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DiscoverActivity.class));
            }
        });
        searchView = view.findViewById(R.id.search_cardView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DiscoverActivity.class));
            }
        });

        gridLayoutManager = new GridLayoutManager(getContext(),
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getInt(THUMBNAIL_SHARED_PREFERENCE_KEY, 2));

        loadingProgress = view.findViewById(R.id.loading_progress_bar);
        discoverRecyclerView = view.findViewById(R.id.discover_recycler_view);
        discoverRecyclerView.setLayoutManager(gridLayoutManager);
        discoverRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    onScrollChanged.onScroll(true);
//                    Log.i(TAG, "Scrolling up");
                    searchView.setVisibility(View.GONE);
                } else {
                    onScrollChanged.onScroll(false);
                    searchView.setVisibility(View.VISIBLE);
//                    Log.i(TAG, "Scrolling down");
                }
            }
        });

        discoverRecyclerView.setAdapter(new DiscoverAdapter(list));
        loadingProgress.setVisibility(View.VISIBLE);
        discoverRecyclerView.setVisibility(View.GONE);

        OriginalImageDAO.getInstance(getContext()).getImages(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onScrollChanged = (FeedInterfaces.OnScrollChanged) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onScrollChanged = null;
    }

    @Override
    public void onImagesRetrieveSuccess(List<OriginalImageDTO> imageDTOList) {
        Collections.shuffle(imageDTOList);
        this.imageDTOList = imageDTOList;

        discoverRecyclerView.setAdapter(new DiscoverAdapter(imageDTOList));
        discoverRecyclerView.setHasFixedSize(true);
        loadingProgress.setVisibility(View.GONE);
        discoverRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.feed_menu_action_sort).setVisible(false);
//        menu.findItem(R.id.feed_menu_action_view).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onImageRetrieveFailed() {
        Toasty.warning(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
    }

    public void thumbnailSizeChanged() {

        if (imageDTOList != null && !imageDTOList.isEmpty()) {
            gridLayoutManager = new GridLayoutManager(getContext(),
                    PreferenceManager.getDefaultSharedPreferences(getContext())
                            .getInt(THUMBNAIL_SHARED_PREFERENCE_KEY, 2));
            discoverRecyclerView.setLayoutManager(gridLayoutManager);
            discoverRecyclerView.setAdapter(new DiscoverAdapter(imageDTOList));
        }
    }

}
