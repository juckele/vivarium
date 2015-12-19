package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.TestDatabase;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class WorkerPersistenceTest
{
    @Test
    public void testPersistAndFetch() throws SQLException
    {
        TestDatabase.initializeTestDatabase();
        try (Connection databaseConnection = DatabaseUtils.createDatabaseConnection(TestDatabase.TEST_DATABASE_NAME,
                TestDatabase.TEST_DATABASE_USER, TestDatabase.TEST_DATABASE_PASSWORD);)
        {
            UUID id = UUID.randomUUID();
            WorkerModel initial = new WorkerModel(id, new long[] { 100, 150, 200, 210 }, true,
                    new Timestamp(System.currentTimeMillis()), Version.FILE_FORMAT_VERSION, Version.CURRENT_VERSION);
            initial.persistToDatabase(databaseConnection);
            WorkerModel fetched = WorkerModel.getFromDatabase(databaseConnection, id).get();
            Tester.isTrue("The worker we fetched should be the same as the worker we started with: ",
                    fetched.equals(initial));
        }
    }
}
