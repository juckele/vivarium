package io.vivarium.db;

import java.sql.SQLException;

public interface DatabaseConnection
{
    boolean isConnected() throws SQLException;
}
