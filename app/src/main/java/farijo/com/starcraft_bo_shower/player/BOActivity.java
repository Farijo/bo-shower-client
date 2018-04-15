package farijo.com.starcraft_bo_shower.player;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import farijo.com.starcraft_bo_shower.R;
import farijo.com.starcraft_bo_shower.xml_formatting.BOXmlParser;

public class BOActivity extends AppCompatActivity {

    public static final String BO_EXTRA = "BO";

    public Progresser currentProgress;

    private boolean optionOpen = true;
    private ValueAnimator currentAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView title = findViewById(R.id.bo_title);

        bindOptionPanelAnimation(R.id.title_panel, R.id.arrow, R.id.option_panel);

        final BuildOrderAdapter adapter = new BuildOrderAdapter();
        boolean playEnabled = true;

        if(getIntent().hasExtra(BO_EXTRA)) {
            File file = new File(getIntent().getStringExtra(BO_EXTRA));
            title.setText(removeFileExtensions(file.getName()));
            try {
                BOXmlParser parser = new BOXmlParser();
                parser.parseBO(this, file);
                if(!parser.areAllTimingsAvailable()) {
                    adapter.disableTimings();
                }
                adapter.setActions(parser.getActions());
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        }

        RecyclerView recycler = findViewById(R.id.building);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
        recycler.setAdapter(adapter);

        bindOptionScrolling(R.id.start_button, R.id.autoscroll_selector, adapter, playEnabled);
        bindOptionShowTimer(R.id.show_timer, adapter, playEnabled);
    }

    @Override
    protected void onDestroy() {
        if (currentProgress != null) {
            currentProgress.cancel();
        }
        super.onDestroy();
    }

    private static String removeFileExtensions(String fileName) {
        return fileName.substring(0, fileName.indexOf('.'));
    }

    private void bindOptionPanelAnimation(@IdRes int titleViewId, @IdRes int tabImg, @IdRes int optViewId) {
        final View titleView = findViewById(titleViewId);
        final ImageView stateTabIndicator = findViewById(tabImg);
        final View optionView = findViewById(optViewId);

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) optionView.getLayoutParams();
        ValueAnimator.AnimatorUpdateListener optionPanTranslation = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.topMargin = (int) (float) valueAnimator.getAnimatedValue();
                optionView.requestLayout();
            }
        };

        final ValueAnimator enter = new ValueAnimator();
        final ValueAnimator exit = new ValueAnimator();
        enter.addUpdateListener(optionPanTranslation);
        exit.addUpdateListener(optionPanTranslation);

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionOpen = !optionOpen;
                if (currentAnim != null) {
                    currentAnim.cancel();
                }
                if (optionOpen) {
                    enter.setFloatValues(params.topMargin, 0);
                    currentAnim = enter;
                    stateTabIndicator.setImageResource(R.drawable.arrow_bottom);
                } else {
                    exit.setFloatValues(params.topMargin, -optionView.getHeight());
                    currentAnim = exit;
                    stateTabIndicator.setImageResource(R.drawable.arrow_right);
                }
                currentAnim.start();
            }
        });
    }

    private void bindOptionScrolling(@IdRes int btnStart, @IdRes int cbAutoScroll,
                                     final BuildOrderAdapter adapter, final boolean playEnabled) {
        final CheckBox autoScroll = findViewById(cbAutoScroll);
        final View startBtn = findViewById(btnStart);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    adapter.startTimer(
                            findViewById(R.id.timer),
                            (NestedScrollView) findViewById(R.id.root_scroll),
                            autoScroll,
                            BOActivity.this);
                } catch (NoSuchElementException e) {
                    finish();
                    return;
                }
                TextView textView = ((TextView) v);
                if (textView.getText().equals("Start")) {
                    textView.setText("Stop");
                } else {
                    textView.setText("Start");
                }
            }
        });
        startBtn.setEnabled(playEnabled);
        autoScroll.setChecked(playEnabled);
        autoScroll.setEnabled(playEnabled);
    }

    private void bindOptionShowTimer(@IdRes int cbShowTimer, final BuildOrderAdapter adapter, final boolean playEnabled) {
        final CheckBox showTimerBox = findViewById(cbShowTimer);

        showTimerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adapter.showTimers(b);
            }
        });
        showTimerBox.setChecked(playEnabled);
        showTimerBox.setEnabled(playEnabled);
    }
}
