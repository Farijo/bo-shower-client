package farijo.com.starcraft_bo_shower.player;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import farijo.com.starcraft_bo_shower.R;

public class BOCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bocreation);

        final BuildOrderAdapter boAdapter = new BuildOrderAdapter();
        RecyclerView itemCreated = findViewById(R.id.item_created);
        itemCreated.setLayoutManager(new LinearLayoutManager(this));
        itemCreated.setAdapter(boAdapter);

        final Spinner dropdown = findViewById(R.id.entity);
        final Field[] fields = R.drawable.class.getDeclaredFields();
        final List<String> items = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            final String name = fields[i].getName();
            if (name.contains("protoss") || name.contains("terran") || name.contains("zerg")) {
                items.add(name);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        final ImageView icon = findViewById(R.id.entity_icon);
        final TextView pop = findViewById(R.id.pop);
        final TextView timing = findViewById(R.id.timing);

        final View add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SC2Action a = new SC2Action();
                a.population = pop.getText().toString();
                a.strTiming = timing.getText().toString();
                a.name = items.get(dropdown.getSelectedItemPosition());
                a.resourceIcon = getResources().getIdentifier(a.name, "drawable", getPackageName());
                boAdapter.addAction(a);
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    icon.setImageResource(getResources().getIdentifier(items.get(position), "drawable", getPackageName()));
                } catch (Resources.NotFoundException ignored) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
