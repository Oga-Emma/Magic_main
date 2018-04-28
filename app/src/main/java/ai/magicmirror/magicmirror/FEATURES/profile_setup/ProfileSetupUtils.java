package ai.magicmirror.magicmirror.FEATURES.profile_setup;

import android.content.Context;
import android.graphics.drawable.Drawable;

import ai.magicmirror.magicmirror.R;

public class ProfileSetupUtils {

    public static final String COMPLEXTION_DARK = "dark";
    public static final String COMPLEXTION_BROWN = "brown";
    public static final String COMPLEXTION_WHITE = "white";

    public static final String OVAL = "oval";
    public static final String ROUND = "round";
    public static final String SQUARE = "square";
    public static final String OBLONG = "oblong";
    public static final String HEART = "heart";
    public static final String DIAMOND = "diamond";

    public static final Drawable getComplextionResourceImage(String complextion, Context context){
        switch (complextion){
            case COMPLEXTION_DARK: return context.getResources().getDrawable(R.drawable.dark_skin_background);
            case COMPLEXTION_BROWN: return context.getResources().getDrawable(R.drawable.brown_skin_background);
            case COMPLEXTION_WHITE: return context.getResources().getDrawable(R.drawable.white_skin_background);
            default: return context.getResources().getDrawable(R.drawable.brown_skin_background);
        }
    }

    public static int getFaceShape(String shape, Context context) {

        switch (shape){
            case DIAMOND: return R.drawable.diamond;
            case OVAL: return R.drawable.oval;
            case ROUND: return R.drawable.round;
            case SQUARE: return R.drawable.square;
            case OBLONG: return R.drawable.oblong;
            case HEART: return R.drawable.heart;
            default: return R.drawable.oval;
        }
    }
}
