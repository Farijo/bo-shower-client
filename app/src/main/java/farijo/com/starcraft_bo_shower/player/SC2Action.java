package farijo.com.starcraft_bo_shower.player;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * Created by Teddy on 21/07/2017.
 */

public class SC2Action {

    @DrawableRes
    public static int getDrawableId(Context context, String value) {
        return context.getResources().getIdentifier(value, "drawable", context.getPackageName());
    }

    public static final int NO_TIMING = -1;
    public static final int INDEPENDENT = -1;

    public String population;
    public String strTiming;
    public int onFinish = INDEPENDENT;
    public long deltaTiming;
    public int timing = NO_TIMING;
    public String name;
    public int resourceIcon = 0;
    public int count = 1;
    public String details;
    View view;

    public boolean isDepedentOfEntity() {
        return onFinish > INDEPENDENT;
    }

    @Override
    public String toString() {
        return population+" "+timing+" "+name+" "+details;
    }
}
