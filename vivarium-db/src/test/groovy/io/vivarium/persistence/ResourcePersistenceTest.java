package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.TestDatabase;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class ResourcePersistenceTest
{
    @Test
    public void testPersistAndFetch() throws SQLException
    {
        TestDatabase.initializeTestDatabase();
        try (Connection databaseConnection = DatabaseUtils.createDatabaseConnection(TestDatabase.TEST_DATABASE_NAME,
                TestDatabase.TEST_DATABASE_USER, TestDatabase.TEST_DATABASE_PASSWORD);)
        {
            UUID id = UUID.randomUUID();
            VivariumObjectCollection collection = new VivariumObjectCollection();
            ResourceModel initial = new ResourceModel(id, JSONConverter.serializerToJSONString(collection),
                    Version.FILE_FORMAT_VERSION);
            initial.persistToDatabase(databaseConnection);
            ResourceModel fetched = ResourceModel.getFromDatabase(databaseConnection, id).get();
            Tester.isTrue("The resource we fetched should be the same as the worker we started with: ",
                    fetched.equals(initial));
        }
    }
}
