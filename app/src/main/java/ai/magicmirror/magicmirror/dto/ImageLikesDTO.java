package ai.magicmirror.magicmirror.dto;

import java.util.ArrayList;
import java.util.List;

public class ImageLikesDTO {
    int likeCount;
    List<String> userIds;

    public ImageLikesDTO() {
        this.likeCount = 0;
        this.userIds = new ArrayList<>();
    }

    public ImageLikesDTO(int likeCount, List<String> userIds) {
        this.likeCount = likeCount;
        this.userIds = userIds;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
