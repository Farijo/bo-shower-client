package farijo.com.starcraft_bo_shower;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.SQLException;

import farijo.com.starcraft_bo_shower.network.bo_database.BODatabase;

public class BODatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodatabase);

        BODatabase database = new BODatabase();
        database.initConnexion();
        database.requestBO("");
    }
}
