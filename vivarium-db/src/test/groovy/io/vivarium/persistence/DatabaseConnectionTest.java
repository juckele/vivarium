package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.TestDatabase;

public class DatabaseConnectionTest
{
    @Test
    public void testUpdateJobStatuses() throws SQLException
    {
        TestDatabase.initializeTestDatabase();
        try (Connection databaseConnection = DatabaseUtils.createDatabaseConnection(TestDatabase.TEST_DATABASE_NAME,
                TestDatabase.TEST_DATABASE_USER, TestDatabase.TEST_DATABASE_PASSWORD);)
        {
            Tester.isNotNull("Database connection is not null: ", databaseConnection);
        }
    }
}
