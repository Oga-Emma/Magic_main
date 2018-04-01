package ai.magicmirror.magicmirror.features.feeds;

import java.util.List;

import ai.magicmirror.magicmirror.models.FeedDTO;

/**
 * Created by seven on 3/30/18.
 */

public class FeedPresenter implements FeedMVP.Presenter {

    private FeedMVP.View view;
    private FeedMVP.Repository repository;

    public FeedPresenter(FeedMVP.View view, FeedMVP.Repository repository){

        this.view = view;
        this.repository = repository;
    }

    @Override
    public void loadFeeds(String category) {
        List<FeedDTO> feeds = repository.getFeeds(category);

        if(feeds.size() > 0)
            view.displayFeeds(feeds);

        else
            view.displayNoFeedError("Error getting feed");

    }



}
