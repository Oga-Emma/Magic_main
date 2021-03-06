package ai.magicmirror.magicmirror.utils;

/**
 * Created by seven on 4/1/18.
 */

public class FaceUtils {
    public static final int OVAL = 1;
    public static final int ROUND = 2;
    public static final int SQUARE = 3;
    public static final int OBLONG = 4;
    public static final int HEART = 5;
    public static final int DIAMOND = 6;

    public static String getFaceShapeName(int faceShape){
        switch (faceShape){
            case OVAL: return "Oval";
            case ROUND: return "Round";
            case SQUARE: return "Square";
            case OBLONG: return "Oblong";
            case HEART: return "Heart";
            case DIAMOND: return "Diamond";
            default: return "Unknown";
        }
    }
    public static int getFaceShape(String faceShape){
        switch (faceShape){
            case "Oval": return OVAL;
            case "Round": return ROUND;
            case "Square": return SQUARE;
            case "Oblong": return OBLONG;
            case "Heart": return HEART;
            case "Diamond": return DIAMOND;
            default: return 1;
        }
    }

    public static class Faceshapes{
        public static final String OVAL = "oval";
        public static final String ROUND = "round";
        public static final String SQUARE = "square";
        public static final String OBLONG = "oblong";
        public static final String HEART = "heart";
        public static final String DIAMOND = "diamond";
        public static final String UNKNOWN = "unknown";
    }

    public static class Complexion{
        public static final String COMPLEXTION_DARK = "dark";
        public static final String COMPLEXTION_BROWN = "brown";
        public static final String COMPLEXTION_WHITE = "white";
        public static final String UKNOWN = "unknown";
    }
}
