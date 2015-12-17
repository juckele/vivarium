package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.TestConstants;

public class JobPersistenceTest
{
    @Test
    public void testUpdateJobStatuses() throws SQLException
    {
        Connection databaseConnection = DatabaseUtils.createDatabaseConnection(TestConstants.TEST_DATABASE_NAME,
                TestConstants.TEST_DATABASE_USER, TestConstants.TEST_DATABASE_PASSWORD);
        databaseConnection.close();
    }
}
