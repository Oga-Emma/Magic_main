package ai.magicmirror.magicmirror.dao;

import ai.magicmirror.magicmirror.dto.UserDTO;

public class UserDAO {

    //create user[signIn (phone number signin, google signin)]
    public void signInWithPhone() {

    }

    public void signInWithGoogle() {

    }

    //read user [UserDTO]
    public UserDTO getSignInUserInfo() {
        return null;
    }

    //update user info
    public void updateUserInfo(UserDTO user) {

    }

    //sign out
    public void signOut() {

    }


    interface OnPhoneSignIn {
        void onPhoneSignInSuccess();

        void onPhoneSignInSfailed();
    }

    interface OnGoogleSignIn {
        void onGoogleSignInSuccess();

        void onGoogleSignInfailed();
    }

    interface OnGetUser {
        void onGetUserSuccess();

        void onGetUserfailed();
    }

    interface OnUpdateUser {
        void onUpdateUserSuccess();

        void onUpdateUserfailed();
    }

    interface OnSignOut {
        void onSignOutSuccess();

        void onSignOutfailed();
    }
}
