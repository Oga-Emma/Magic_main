package ai.magicmirror.magicmirror.models;

/**
 * Created by seven on 3/30/18.
 */

public class UserDTO {
    private String userName;
    private String profileImageUrl;
    private int faceShape;

    public UserDTO() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getFaceShape() {
        return faceShape;
    }

    public void setFaceShape(int faceShape) {
        this.faceShape = faceShape;
    }
}
