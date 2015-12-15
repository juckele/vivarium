package io.vivarium.persistence;

import java.sql.Connection;

import io.vivarium.persistence.model.PersistenceModel;

public class PersistenceModule
{
    private final Connection _databaseConnection;

    public PersistenceModule(Connection databaseConnection)
    {
        _databaseConnection = databaseConnection;
    }

    public void persist(PersistenceModel model)
    {

    }
}
