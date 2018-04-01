package ai.magicmirror.magicmirror.features.feeds;

import android.telephony.PhoneNumberUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.magicmirror.magicmirror.models.FeedDTO;

import static org.junit.Assert.*;

/**
 * Created by seven on 3/30/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class FeedPresenterTest {

    private FeedMVP.Presenter presenter;
    @Mock FeedMVP.Repository repository;
    @Mock FeedMVP.View view;
    @Mock PhoneNumberUtils utils;


    @Before
    public void setUp(){
        presenter = new FeedPresenter(view, repository);
    }

    @Test
    public void loadFeedsWithBooks(){
//        Assert.assertTrue(true);

        //given
        List<FeedDTO> MANY_FEED = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            MANY_FEED.add(new FeedDTO());
        }

        Mockito.when(repository.getFeeds(FeedUtils.GELE_FEED))
                .thenReturn(MANY_FEED);

        //when
        presenter.loadFeeds(FeedUtils.GELE_FEED);

        //then
        Mockito.verify(view).displayFeeds(MANY_FEED);
    }

    @Test
    public void loadFeedsWithNoBook(){
//        Assert.assertTrue(true);

        //given
        Mockito.when(repository.getFeeds(FeedUtils.GELE_FEED))
                .thenReturn(Collections.EMPTY_LIST);

        //when
        presenter.loadFeeds(FeedUtils.GELE_FEED);

        //then
        Mockito.verify(view).displayNoFeedError("Error getting feed");
    }


    @After
    public void tearDown(){
    }

}