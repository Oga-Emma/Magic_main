package ai.magicmirror.magicmirror.utils;

/**
 * Created by seven on 4/2/18.
 */

public class FirebaseUtils {

    public static final String ORIGINAL_IMAGE_LOCATON = "original_images";
    public static final String USER_PROFILE_IMAGE__FULL_IMAGE_LOCATON = "user_profile_image/full_image";
    public static final String USER_PROFILE_IMAGE__THUMBNAIL_LOCATON = "user_profile_image/thumnail";
    public static final String IMAGE_LIKES_LOCATION = "image_likes";

    public static class Database{
        public static final String _USERS_TABLE = "users";
        public static final String USERNAME_USERS_TABLE = "username";
        public static final String FACESHAPE_USERS_TABLE = "faceshape";
        public static final String FACECOLOR_USERS_TABLE = "facecolor";
        public static final String PROFILE_PICTURE_FULL_USERS_TABLE = "profile_picture_full";
        public static final String PROFILE_PICTURE_THUMBNAIL_USERS_TABLE = "profile_picture_thumbnail";
        public static final String UPLOADED_IMAGE_USERS_TABLE = "uploaded_images";

    }
}
