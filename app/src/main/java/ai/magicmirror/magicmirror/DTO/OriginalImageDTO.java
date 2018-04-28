package ai.magicmirror.magicmirror.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OriginalImageDTO implements Parcelable {
    private String imageId;
    private String imageUrl;
    private String complexion;
    private String faceshape;
    private String hairStyle;
    private int noOfSwaps;
    @Exclude private int noOfLikes;

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public OriginalImageDTO(String imageId, String imageUrl, String complexion,
                            String faceshape, String hairStyle, int noOfSwaps) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.complexion = complexion;
        this.faceshape = faceshape;
        this.hairStyle = hairStyle;
        this.noOfSwaps = noOfSwaps;
    }

    public OriginalImageDTO() {
        this.imageId = UUID.randomUUID().toString();
        this.imageUrl = "";
        this.complexion = "";
        this.faceshape = "";
        this.hairStyle = "";
        this.noOfSwaps = 0;
    }


    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getComplexion() {
        return complexion;
    }

    public void setComplexion(String complexion) {
        this.complexion = complexion;
    }

    public String getFaceshape() {
        return faceshape;
    }

    public void setFaceshape(String faceshape) {
        this.faceshape = faceshape;
    }

    public String getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(String hairStyle) {
        this.hairStyle = hairStyle;
    }

    public double getNoOfSwaps() {
        return noOfSwaps;
    }

    public void setNoOfSwaps(int noOfSwaps) {
        this.noOfSwaps = noOfSwaps;
    }

    @Override
    public String toString() {
        return getImageId() + "\n" + getImageUrl();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageId);
        dest.writeString(this.imageUrl);
        dest.writeString(this.complexion);
        dest.writeString(this.faceshape);
        dest.writeString(this.hairStyle);
        dest.writeDouble(this.noOfSwaps);
        dest.writeDouble(this.noOfLikes);
    }

    protected OriginalImageDTO(Parcel in) {
        this.imageId = in.readString();
        this.imageUrl = in.readString();
        this.complexion = in.readString();
        this.faceshape = in.readString();
        this.hairStyle = in.readString();
        this.noOfSwaps = in.readInt();
        this.noOfLikes = in.readInt();
    }

    public static final Creator<OriginalImageDTO> CREATOR = new Creator<OriginalImageDTO>() {
        @Override
        public OriginalImageDTO createFromParcel(Parcel source) {
            return new OriginalImageDTO(source);
        }

        @Override
        public OriginalImageDTO[] newArray(int size) {
            return new OriginalImageDTO[size];
        }
    };
}
