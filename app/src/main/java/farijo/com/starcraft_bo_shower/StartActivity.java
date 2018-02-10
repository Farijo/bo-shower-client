package farijo.com.starcraft_bo_shower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.IOException;

import farijo.com.starcraft_bo_shower.file_explorer.BOExplorerActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        findViewById(R.id.searchBO).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, BOExplorerActivity.class));
            }
        });

        RecyclerView recycler = findViewById(R.id.liste);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        BuildListAdapter adapter = new BuildListAdapter(this);
        recycler.setAdapter(adapter);

        try {
            final String END_XML = ".sc2bo.xml";
            for (String s : getAssets().list("")) {
                if(s.contains(END_XML)) {
                    adapter.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
