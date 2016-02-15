package io.vivarium.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class DatabaseConnectionTest
{
    @Test
    public void testSchemaFile()
    {
        TestDatabase.initializeTestDatabase();
        Tester.pass(
                "The database has been successfully initialized, which means the schema.yaml file is well formatted.");
    }

    @Test
    public void testConnect() throws SQLException
    {
        TestDatabase.initializeTestDatabase();
        try (Connection databaseConnection = DatabaseUtils.createDatabaseConnection(TestDatabase.TEST_DATABASE_NAME,
                TestDatabase.TEST_DATABASE_USER, TestDatabase.TEST_DATABASE_PASSWORD);)
        {
            Tester.isNotNull("Database connection is not null: ", databaseConnection);
        }
    }
}
