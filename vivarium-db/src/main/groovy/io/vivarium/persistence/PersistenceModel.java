package io.vivarium.persistence;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Connection;
import java.sql.SQLException;

@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class PersistenceModel
{
    protected abstract void persistToDatabase(Connection connection) throws SQLException;
}
