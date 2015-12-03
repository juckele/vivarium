package io.vivarium.db;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.johnuckele.vtest.Tester;

public class DatabaseTest
{
    @Test
    public void testDatabaseCreateAndConnect()
    {
        Injector injector = Guice.createInjector(new DatabaseModule());
        Database database = injector.getInstance(Database.class);
        Tester.isNotNull("Database should not be null", database);
    }
}
