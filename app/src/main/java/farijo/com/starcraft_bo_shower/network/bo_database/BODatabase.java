package farijo.com.starcraft_bo_shower.network.bo_database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Eikichi on 26/02/2018.
 */

public class BODatabase {

    private Connection connection;

    public void initConnexion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://192.168.1.43:3306/bo_database_repo", "diugh", null);
                    notifyAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void requestBO(final String boName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    while (connection == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    String boRequest =
                            "SELECT * FROM bo_item " +
                                    "INNER JOIN bo_item_set ON bo_item.id = bo_item_set.bo_item_id " +
                                    "INNER JOIN bo ON bo_item_set.id = bo.bo_item_set_id " +
                                    "WHERE bo.build_name = ?";
                    final PreparedStatement statement = connection.prepareStatement(boRequest);
                    statement.setString(1, boName);
                    Log.d("odfidoug", "requestBO: " + statement.executeQuery());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
