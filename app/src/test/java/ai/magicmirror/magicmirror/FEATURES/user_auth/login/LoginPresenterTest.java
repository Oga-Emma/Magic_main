package ai.magicmirror.magicmirror.FEATURES.user_auth.login;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by seven on 3/30/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    LoginMVP.Repository repository;

    @Mock
    LoginMVP.View view;

    @Test
    public void shouldPass(){
        Assert.assertEquals(1, 1);
    }

    @Test
    public void phoneSignInSuccess(){

        //given
        String phoneNumber = "07012446202";
        UserDTO user = new UserDTO();
        Mockito.when(repository.phoneNumberAuth(phoneNumber))
                .thenReturn(user);

        //when
        LoginPresenter presenter = new LoginPresenter(view, repository);
        presenter.phoneNumberSignIn(phoneNumber);

        //then
        Mockito.verify(view).signInSuccess(user);

    }

    @Test
    public void phoneSignInFailure(){

        //given
        Mockito.when(repository.phoneNumberAuth(""))
                .thenReturn(null);

        //when
        LoginPresenter presenter = new LoginPresenter(view, repository);
        presenter.phoneNumberSignIn("");

        //then
        Mockito.verify(view).signInFailed("Error signing in");

    }

    @Test
    public void googleSignInSuccess() {

        //given

        //when

        //then

    }

    @Test
    public void googleSignInFailure(){

        //given

        //when

        //then

    }
}