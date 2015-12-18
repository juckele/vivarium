package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import io.vivarium.db.TestDatabase;

public class WorkerPersistenceTest
{
    @Test
    public void testUpdateJobStatuses() throws SQLException
    {
        TestDatabase.initializeTestDatabase();
        try (Connection databaseConnection = TestDatabase.getConnection())
        {
        }
    }
}
