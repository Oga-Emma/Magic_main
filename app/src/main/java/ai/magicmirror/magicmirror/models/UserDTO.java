package ai.magicmirror.magicmirror.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by seven on 3/30/18.
 */

public class UserDTO {
    private String userName;
    private String fullProfileImageUrl;
    private String thumbnailProfileImageUrl;
    private int faceShape;
    private int faceColor;
    private List<UUID> swapsId;


    public UserDTO() {
        userName = "";
        fullProfileImageUrl = "";
        thumbnailProfileImageUrl ="";
        faceShape = 0;
        faceColor = 0;
        swapsId = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullProfileImageUrl() {
        return fullProfileImageUrl;
    }

    public void setFullProfileImageUrl(String fullProfileImageUrl) {
        this.fullProfileImageUrl = fullProfileImageUrl;
    }

    public String getThumbnailProfileImageUrl() {
        return thumbnailProfileImageUrl;
    }

    public void setThumbnailProfileImageUrl(String thumbnailProfileImageUrl) {
        this.thumbnailProfileImageUrl = thumbnailProfileImageUrl;
    }

    public int getFaceShape() {
        return faceShape;
    }

    public void setFaceShape(int faceShape) {
        this.faceShape = faceShape;
    }

    public int getFaceColor() {
        return faceColor;
    }

    public void setFaceColor(int faceColor) {
        this.faceColor = faceColor;
    }

    public List<UUID> getSwapsId() {
        return swapsId;
    }

    public void setSwapsId(List<UUID> swapsId) {
        this.swapsId = swapsId;
    }
}
