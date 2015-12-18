package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.TestDatabase;
import io.vivarium.util.UUID;

public class JobPersistenceTest
{
    @Test
    public void testPersistAndFetch() throws SQLException
    {
        TestDatabase.initializeTestDatabase();
        try (Connection databaseConnection = DatabaseUtils.createDatabaseConnection(TestDatabase.TEST_DATABASE_NAME,
                TestDatabase.TEST_DATABASE_USER, TestDatabase.TEST_DATABASE_PASSWORD);)
        {
            UUID jobUUID = UUID.randomUUID();
            JobModel initialJob = new RunSimulationJobModel(jobUUID, JobStatus.BLOCKED, 0, null, null, null, 0,
                    new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
            initialJob.persistToDatabase(databaseConnection);
            JobModel fetchedJob = JobModel.getFromDatabase(databaseConnection, jobUUID).get();
            Tester.isTrue("The job we fetched should be the same as the job we didn't fetch",
                    fetchedJob.equals(initialJob));
        }
    }
}
