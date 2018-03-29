package farijo.com.starcraft_bo_shower.network.bo_database;

import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://192.168.0.13:3306/bo_database_repo", "database_client", null);
                    synchronized (BODatabase.this) {
                        BODatabase.this.notifyAll();
                    }
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
                synchronized (BODatabase.this) {
                    while (connection == null) {
                        try {
                            BODatabase.this.wait();
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
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        int id  = res.getInt(1);
                        int pop = res.getInt(2);
                        Date time = res.getDate(3);
                        int entityId = res.getInt(4);
                        String details = res.getString(5);
                        Log.d("dfgdgdg", "run: " + id +" | "+ pop +" | "+ time +" | "+ entityId +" | "+ details);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
