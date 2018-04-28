package ai.magicmirror.magicmirror.features.feed;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.dao.OriginalImageDAO;
import ai.magicmirror.magicmirror.dto.OriginalImageDTO;
import es.dmoral.toasty.Toasty;

import static ai.magicmirror.magicmirror.features.feed.FeedPageDiscoverFragment.THUMBNAIL_SHARED_PREFERENCE_KEY;

public class DiscoverActivity extends AppCompatActivity implements View.OnClickListener, OriginalImageDAO.OnImageRetrieve {

    public static final String GELE = "Gele";
    public static final String BRAID = "Braid";
    public static final String WEAVON = "Weavon";
    public static final String WEAVING = "Weaving";
    public static final String ATTACHMENT = "Attachment";
    public static final String HAIRCUT = "Haircut";
    public static final String NATURAL = "Natural";
    private static final String TAG = DiscoverActivity.class.getSimpleName();
    List<String> searchHairstyles;
    RecyclerView discoverRecyclerView;
    ProgressBar loadingProgress;
    private AutoCompleteTextView editText;
    private List<OriginalImageDTO> imageDTOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_activity);

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        EditText editText = toolbar.findViewById(R.id.search_editText);

        toolbar.findViewById(R.id.back_image_view).setOnClickListener(this);
        toolbar.findViewById(R.id.search_image_view).setOnClickListener(this);

        String[] hairSearchHint = {GELE, WEAVING, WEAVON, BRAID, ATTACHMENT, HAIRCUT, NATURAL};

        editText = toolbar.findViewById(R.id.discover_autoCompleteTextView);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hairSearchHint);
        editText.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fetchSearchResult(getSearchHairstyle(s.toString().toLowerCase()));
            }
        });

        searchHairstyles = new ArrayList<>();

        loadingProgress = findViewById(R.id.loading_progress_bar);
        discoverRecyclerView = findViewById(R.id.discover_discover_recycler_view);
        discoverRecyclerView.setLayoutManager(new GridLayoutManager(this,
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt(THUMBNAIL_SHARED_PREFERENCE_KEY, 2)));

        discoverRecyclerView.setAdapter(new DiscoverAdapter(Collections.emptyList()));
        loadingProgress.setVisibility(View.VISIBLE);
        discoverRecyclerView.setVisibility(View.GONE);

        OriginalImageDAO.getInstance(this).getImages(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_image_view) {
            finish();
        } else if (v.getId() == R.id.search_image_view) {

            fetchSearchResult(getSearchHairstyle(editText.getText().toString().toLowerCase()));
        }
    }

    private List<String> getSearchHairstyle(String s) {
        searchHairstyles.clear();

        if (WEAVON.toLowerCase().contains(s)) {
            searchHairstyles.add(WEAVON);
        }
        if (WEAVING.toLowerCase().contains(s)) {
            searchHairstyles.add(WEAVING);
        }
        if (BRAID.toLowerCase().contains(s)) {
            searchHairstyles.add(BRAID);
        }
        if (ATTACHMENT.toLowerCase().contains(s)) {
            searchHairstyles.add(ATTACHMENT);
        }
        if (HAIRCUT.toLowerCase().contains(s)) {
            searchHairstyles.add(HAIRCUT);
        }
        if (NATURAL.toLowerCase().contains(s)) {
            searchHairstyles.add(NATURAL);
        }

        return searchHairstyles;
    }

    private void fetchSearchResult(List<String> searchHairstyle) {
        loadingProgress.setVisibility(View.VISIBLE);
        discoverRecyclerView.setVisibility(View.GONE);

        List<OriginalImageDTO> searchResult = new ArrayList<>();

        if (null != this.imageDTOList) {

            if (searchHairstyle.size() > 0) {
                for (String str : searchHairstyle) {
//            Log.i(TAG, str);

                    for (OriginalImageDTO image : imageDTOList) {
                        if (image.getHairStyle().contains(str.toLowerCase()))
                            searchResult.add(image);
                    }
                }

                Collections.shuffle(searchResult);
                discoverRecyclerView.setAdapter(new DiscoverAdapter(searchResult));
                loadingProgress.setVisibility(View.GONE);
                discoverRecyclerView.setVisibility(View.VISIBLE);
            } else {
                Collections.shuffle(imageDTOList);
                discoverRecyclerView.setAdapter(new DiscoverAdapter(imageDTOList));
                loadingProgress.setVisibility(View.GONE);
                discoverRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onImagesRetrieveSuccess(List<OriginalImageDTO> imageDTOList) {
        this.imageDTOList = imageDTOList;

        Collections.shuffle(imageDTOList);
        discoverRecyclerView.setAdapter(new DiscoverAdapter(imageDTOList));
        loadingProgress.setVisibility(View.GONE);
        discoverRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onImageRetrieveFailed() {
        Toasty.warning(this, "Error loading image", Toast.LENGTH_SHORT).show();
    }
}
