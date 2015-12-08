/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;

@AutoFactory(implementing = DatabaseConnectionFactory.class)
public class MockDatabaseConnection implements DatabaseConnection
{
    @Inject
    public MockDatabaseConnection(String databaseName, String username, String password)
    {
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }
}
