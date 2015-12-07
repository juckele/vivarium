/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class DatabaseConnectionTest
{
    @Test
    public void testConnectToDatabase()
    {
        DatabaseConnection databaseConnection = DaggerDatabaseSystem.create().connect();
        Tester.isNotNull("DatabaseConnection should be non-null", databaseConnection);
    }
}
