package farijo.com.starcraft_bo_shower.player;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.CheckBox;

import java.lang.ref.WeakReference;
import java.util.Iterator;

/**
 * Created by Teddy on 22/07/2017.
 */

public class Progresser extends CountDownTimer {

    private static long INTERVAL = 40;

    private WeakReference<BOActivity> activity;
    private SC2Action action;
    private Iterator<SC2Action> actionIterator;
    private View timer;
    private NestedScrollView scroller;
    private CheckBox checkBox;
    private int prevHeight;
    private int viewHeight = -1;

    private int oldBackgroundColor;

    public Progresser(SC2Action a, Iterator<SC2Action> it, View t, NestedScrollView s, CheckBox cb, WeakReference<BOActivity> m) {
        super(a.deltaTiming, INTERVAL);
        activity = m;
        activity.get().currentProgress = this;
        action = a;
        actionIterator = it;
        timer = t;
        scroller = s;
        checkBox = cb;
        prevHeight = (int) (action.view.getY());

        oldBackgroundColor = ((ColorDrawable) action.view.getBackground()).getColor();
        action.view.setBackgroundColor(Color.BLUE);
    }

    @Override
    public void onTick(long l) {
        if (viewHeight < 0) {
            viewHeight = action.view.getHeight();
        }
        timer.getLayoutParams().height = prevHeight + (int) (viewHeight - viewHeight * l / action.deltaTiming);
        final long halfFullTime = action.deltaTiming / 2;
        if (l > halfFullTime) {
            l -= halfFullTime;
            timer.setBackgroundColor(Color.rgb(255, (int) (255 - 255 * l / halfFullTime), 0));
        } else {
            timer.setBackgroundColor(Color.rgb((int) (255 * l / halfFullTime), 255, 0));
        }
        timer.requestLayout();
        if (checkBox.isChecked()) {
            scroller.requestDisallowInterceptTouchEvent(true);
            scroller.scrollTo(0, timer.getLayoutParams().height - viewHeight);
        } else {
            scroller.requestDisallowInterceptTouchEvent(false);
        }
    }

    @Override
    public void onFinish() {
        action.view.setBackgroundColor(Color.argb(255, 30, 30, 30));
        if(actionIterator.hasNext() && activity.get() != null) {
            new Progresser(actionIterator.next(), actionIterator, timer, scroller, checkBox, activity).start();
        }
    }

    void rollBackColor() {
        action.view.setBackgroundColor(oldBackgroundColor);
    }
}
