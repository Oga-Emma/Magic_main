package ai.magicmirror.magicmirror.features.feeds;

import java.util.List;

import ai.magicmirror.magicmirror.models.FeedDTO;

/**
 * Created by seven on 3/30/18.
 */

public class FeedMVP {

    interface Model{

    }

    interface View{
        void displayFeeds(List<FeedDTO> feedsList);
        void displayNoFeedError(String errorMessage);
    }

    interface Presenter{
        void loadFeeds(String category);
    }

    interface Repository{
        List<FeedDTO> getFeeds(String category);
    }
}
