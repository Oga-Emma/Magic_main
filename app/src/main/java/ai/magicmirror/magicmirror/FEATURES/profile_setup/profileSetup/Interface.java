package ai.magicmirror.magicmirror.FEATURES.profile_setup.profileSetup;

import android.graphics.Bitmap;

public interface Interface {

    interface OnImageSelectListener{
        void onImageSelected(Bitmap bitmap);
    }


    interface SelectImage{
        void selectImageClicked();
    }


    public interface SetUsernameAndSelfieImage{
        void setUsername(String username);
        void setSelfieImage(Bitmap selfieImageFull, Bitmap selfieImageThumbnail);
    }
}
