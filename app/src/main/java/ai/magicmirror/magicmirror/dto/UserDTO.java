package ai.magicmirror.magicmirror.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDTO implements Parcelable {
    public static final Parcelable.Creator<UserDTO> CREATOR = new Parcelable.Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel source) {
            return new UserDTO(source);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };
    private String userName;
    private String profileImagefullUrl;
    private String profileImageThumbnailUrl;
    private int faceshape;
    private int faceComplexion;
    private long lastSeen;
    private List<String> swapedImageIds;

    public UserDTO(String userName, String profileImagefullUrl, String profileImageThumbnailUrl,
                   int faceshape, int faceComplexion, List<String> swapedImageId) {
        this.userName = userName;
        this.profileImagefullUrl = profileImagefullUrl;
        this.profileImageThumbnailUrl = profileImageThumbnailUrl;
        this.faceshape = faceshape;
        this.faceComplexion = faceComplexion;
        this.swapedImageIds = swapedImageIds;
        this.lastSeen = new Date().getTime();
    }

    public UserDTO() {
        this.userName = "";
        this.profileImagefullUrl = "";
        this.profileImageThumbnailUrl = "";
        this.swapedImageIds = new ArrayList<>();
        this.lastSeen = new Date().getTime();
    }

    protected UserDTO(Parcel in) {
        this.userName = in.readString();
        this.profileImagefullUrl = in.readString();
        this.profileImageThumbnailUrl = in.readString();
        this.faceshape = in.readInt();
        this.faceComplexion = in.readInt();
        this.lastSeen = in.readLong();
        this.swapedImageIds = in.createStringArrayList();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImagefullUrl() {
        return profileImagefullUrl;
    }

    public void setProfileImagefullUrl(String profileImagefullUrl) {
        this.profileImagefullUrl = profileImagefullUrl;
    }

    public String getProfileImageThumbnailUrl() {
        return profileImageThumbnailUrl;
    }

    public void setProfileImageThumbnailUrl(String profileImageThumbnailUrl) {
        this.profileImageThumbnailUrl = profileImageThumbnailUrl;
    }

    public int getFaceshape() {
        return faceshape;
    }

    public void setFaceshape(int faceshape) {
        this.faceshape = faceshape;
    }

    public int getFaceComplexion() {
        return faceComplexion;
    }

    public void setFaceComplexion(int faceComplexion) {
        this.faceComplexion = faceComplexion;
    }

    public List<String> getSwapedImageIds() {
        return swapedImageIds;
    }

    public void setSwapedImageIds(List<String> swapedImageIds) {
        this.swapedImageIds = swapedImageIds;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.profileImagefullUrl);
        dest.writeString(this.profileImageThumbnailUrl);
        dest.writeInt(this.faceshape);
        dest.writeInt(this.faceComplexion);
        dest.writeLong(this.lastSeen);
        dest.writeStringList(this.swapedImageIds);
    }
}
