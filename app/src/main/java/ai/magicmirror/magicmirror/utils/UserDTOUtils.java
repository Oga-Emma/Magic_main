package ai.magicmirror.magicmirror.utils;

public class UserDTOUtils {


    public static class FaceShape{
        public static final int DIAMOND = 1;
        public static final int OVAL = 2;
        public static final int ROUND = 3;
        public static final int SQUARE = 4;
        public static final int OBLONG = 5;
        public static final int HEART = 6;

        public static String getFaceShapeString(int faceshape){
            switch (faceshape){
                case DIAMOND: return "Diamond";
                case OVAL: return "Oval";
                case ROUND: return "Round";
                case SQUARE: return "Square";
                case OBLONG: return "Oblong";
                case HEART: return "Heart";
                default:return "Unknown";
            }
        }
    }

    public static class Complexion{
        public static final int WHITE = 1;
        public static final int BROWN = 2;
        public static final int BLACK = 3;

        public static String getComplexionString(int complexion){
            if(complexion == WHITE){
                return "White";
            }else if(complexion == BROWN){
                return "Brown";
            }else if(complexion == BLACK){
                return "Black";
            }else{
                return "Unknown";
            }
        }
    }
}
