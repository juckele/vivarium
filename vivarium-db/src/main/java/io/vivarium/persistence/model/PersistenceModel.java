package io.vivarium.persistence.model;

import java.sql.Connection;
import java.sql.SQLException;

public interface PersistenceModel
{
    public void persistToDatabase(Connection connection) throws SQLException;
}
