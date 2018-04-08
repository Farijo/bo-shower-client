package farijo.com.starcraft_bo_shower.network.bo_database;

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

    private static final String DEFAULT_IP = "192.168.1.14";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB_NAME = "bo_database_repo";
    private static final String DEFAULT_DB_USERNAME = "bo_user";
    private static final String DEFAULT_DB_PASSWORD = "voidRay";

    private String ip = DEFAULT_IP;
    private String port = DEFAULT_PORT;
    private String dbName = DEFAULT_DB_NAME;
    private String dbUsername = DEFAULT_DB_USERNAME;
    private String dbPassword = DEFAULT_DB_PASSWORD;
    private Connection connection;

    public void initConnexion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ignored) {

                }
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+dbName, dbUsername, dbPassword);
                    wakeUpThreadWaitingConnexion();
                } catch (SQLException ignored) {

                }
            }
        }).start();
    }

    private synchronized void wakeUpThreadWaitingConnexion() {
        BODatabase.this.notifyAll();
    }

    private synchronized void waitConnexion() throws InterruptedException {
        while (connection == null) {
            BODatabase.this.wait();
        }
    }

    public void requestBO(final String boName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    waitConnexion();
                } catch (InterruptedException ignored) {

                }
                try {
                    PreparedStatement request = prepareBORequestQuery(connection, boName);
                    ResultSet res = request.executeQuery();
                    handleBORequestResult(res);
                } catch (SQLException ignored) {

                }
            }
        }).start();
    }

    private static PreparedStatement prepareBORequestQuery(Connection connection, String boName) throws SQLException {
        String boRequest =
                "SELECT * FROM bo_item " +
                        "INNER JOIN bo_item_set ON bo_item.id = bo_item_set.bo_item_id " +
                        "INNER JOIN bo ON bo_item_set.id = bo.bo_item_set_id " +
                        "WHERE bo.build_name = ?";
        final PreparedStatement statement = connection.prepareStatement(boRequest);
        statement.setString(1, boName);
        return statement;
    }

    private static void handleBORequestResult(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id  = resultSet.getInt(1);
            int pop = resultSet.getInt(2);
            Date time = resultSet.getDate(3);
            int entityId = resultSet.getInt(4);
            String details = resultSet.getString(5);
        }
    }
}
