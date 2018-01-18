package farijo.com.starcraft_bo_shower.player;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import farijo.com.starcraft_bo_shower.R;

import static farijo.com.starcraft_bo_shower.player.SC2Action.NO_TIMING;

public class BOActivity extends AppCompatActivity {

    public static final String BO_EXTRA = "BO";

    public Progresser currentProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(getIntent().getStringExtra(BO_EXTRA));

        ((TextView) findViewById(R.id.bo_title)).setText(file.getName());
        final View optionPan = findViewById(R.id.option_panel);
        final ImageView optionArrow = findViewById(R.id.arrow);
        findViewById(R.id.title_panel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionPan.getLayoutParams().height = -2 - optionPan.getLayoutParams().height;
                optionPan.requestLayout();
                if (optionPan.getLayoutParams().height == 0) {
                    optionArrow.setImageResource(R.drawable.arrow_right);
                } else {
                    optionArrow.setImageResource(R.drawable.arrow_bottom);
                }
            }
        });

        final BuildOrderAdapter adapter = new BuildOrderAdapter();

        boolean playEnabled = true;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new FileInputStream(file), "UTF-8");

            int event = parser.getEventType();
            SC2Action currentAction = null;
            String name = null;
            int previousTiming = 0;
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.END_TAG:
                        name = null;
                        break;
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            case "action":
                                if(currentAction != null && currentAction.timing == NO_TIMING) {
                                    playEnabled = false;
                                    adapter.disableTimings();
                                }
                                adapter.add(currentAction);
                                currentAction = new SC2Action();
                                break;
                            case "onfinish":
                                try {
                                    currentAction.onFinish = Integer.parseInt(parser.getAttributeValue(null, "ref"));
                                } catch (NumberFormatException ignored) {
                                }
                                break;
                            case "prod":
                                currentAction.ressourceIcon = SC2Action.getDrawableId(parser.getAttributeValue(null, "image"));
                                try {
                                    currentAction.count = Integer.parseInt(parser.getAttributeValue(null, "count"));
                                } catch (NumberFormatException ignored) {
                                }
                            case "pop":
                            case "time":
                            case "details":
                                name = parser.getName();
                                break;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        final String value = parser.getText().trim();
                        if (name == null || value.isEmpty()) {
                            break;
                        }
                        switch (name) {
                            case "pop":
                                currentAction.population = value;
                                break;
                            case "time":
                                currentAction.strTiming = value;
                                currentAction.timing = Integer.parseInt(value.substring(0, value.indexOf(':'))) * 60 + Integer.parseInt(value.substring(value.indexOf(':') + 1));
                                currentAction.deltaTiming = (long) (1000 * (currentAction.timing - previousTiming) / 10);
                                previousTiming = currentAction.timing;
                                break;
                            case "prod":
                                currentAction.name = value;
                                break;
                            case "details":
                                currentAction.details = value;
                                break;
                        }
                        break;
                }
                event = parser.next();
            }

        } catch (IOException | NullPointerException | XmlPullParserException e) {
            e.printStackTrace();
        }

        RecyclerView recycler = findViewById(R.id.building);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
        recycler.setAdapter(adapter);

        final CheckBox showTimerBox = findViewById(R.id.show_timer);
        final CheckBox autoScroll = findViewById(R.id.autoscroll_selector);

        if(playEnabled) {
            findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
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

            showTimerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    adapter.showTimers(b);
                }
            });
        } else {
            findViewById(R.id.start_button).setEnabled(false);
            autoScroll.setChecked(false);
            autoScroll.setEnabled(false);
            showTimerBox.setChecked(false);
            showTimerBox.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        if(currentProgress != null) {
            currentProgress.cancel();
        }
        super.onDestroy();
    }
}
