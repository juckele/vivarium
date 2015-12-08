/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import java.sql.SQLException;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class DatabaseConnectionTest
{
    @Test
    public void testConnectToDatabaseViaFactory() throws SQLException
    {

        DatabaseConnectionFactory factory = DaggerDatabaseSystem.create().createConnectionFactory();
        DatabaseConnection connection = factory.createConnection("vivarium", "vivarium", "lifetest");
        Tester.isNotNull("DatabaseConnection should be non-null", connection);

        DatabaseConnection badConnection = factory.createConnection("qwerty", "fluffy-bunny", "secret1");
        Tester.isFalse("Bad credentials fail to connect", badConnection.isConnected());
    }

    @Test
    public void testConnectToMockDatabaseViaFactory() throws SQLException
    {
        DatabaseConnectionFactory factory = DaggerTestDatabaseSystem.create().createConnectionFactory();
        DatabaseConnection connection = factory.createConnection("vivarium", "vivarium", "lifetest");
        Tester.isNotNull("DatabaseConnection should be non-null", connection);

        DatabaseConnection badConnection = factory.createConnection("qwerty", "fluffy-bunny", "secret1");
        Tester.isTrue("Bad credentials still work on the mock", badConnection.isConnected());
    }
}
