package io.vivarium.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils
{
    public static Connection createDatabaseConnection(String databaseName, String username, String password)
            throws SQLException
    {
        String url = "jdbc:postgresql://localhost/" + databaseName;
        Connection dbConnection = DriverManager.getConnection(url, username, password);
        return dbConnection;
    }

}
