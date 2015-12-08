package io.vivarium.db.model;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseObjectModel
{
    public void persistToDatabase(Connection connection) throws SQLException;

    public String getTableName();
}
