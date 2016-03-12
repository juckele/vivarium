package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class PersistenceModel
{
    protected abstract void persistToDatabase(Connection connection) throws SQLException;
}
