package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.TestDatabase;
import io.vivarium.test.DatabaseTest;
import io.vivarium.util.UUID;

public class JobPersistenceTest
{
    @Test
    @Category(DatabaseTest.class)
    public void testPersistAndFetch() throws SQLException
    {
        TestDatabase.initializeTestDatabase();
        try (Connection databaseConnection = DatabaseUtils.createDatabaseConnection(TestDatabase.TEST_DATABASE_NAME,
                TestDatabase.TEST_DATABASE_USER, TestDatabase.TEST_DATABASE_PASSWORD))
        {
            UUID id = UUID.randomUUID();
            JobModel initial = new RunSimulationJobModel(id, JobStatus.BLOCKED, 0, null, null, null, 0,
                    new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
            initial.persistToDatabase(databaseConnection);
            JobModel fetched = JobModel.getFromDatabase(databaseConnection, id).get();
            Tester.isTrue("The resource we fetched should be the same as the job we started with: ",
                    fetched.equals(initial));
        }
    }
}
