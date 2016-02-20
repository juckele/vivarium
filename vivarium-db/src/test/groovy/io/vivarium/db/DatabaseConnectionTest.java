package io.vivarium.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.test.DatabaseTest;

public class DatabaseConnectionTest
{
    @Test
    @Category(DatabaseTest.class)
    public void testSchemaFile()
    {
        TestDatabase.initializeTestDatabase();
        Tester.pass(
                "The database has been successfully initialized, which means the schema.yaml file is well formatted.");
    }

    @Test
    @Category(DatabaseTest.class)
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
