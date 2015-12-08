/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;

@AutoFactory(implementing = DatabaseConnectionFactory.class)
public class PostgresDatabaseConnection implements DatabaseConnection
{
    private final Connection _databaseConnection;

    @Inject
    public PostgresDatabaseConnection(String databaseName, String username, String password)
    {
        String url = "jdbc:postgresql://localhost/" + databaseName;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(url, username, password);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            _databaseConnection = connection;
        }
    }

    @Override
    public boolean isConnected() throws SQLException
    {
        return _databaseConnection != null && !_databaseConnection.isClosed();
    }
}
