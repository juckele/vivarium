package io.vivarium.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.johnuckele.vtest.Tester;

public class TestDatabase
{
    public static final String TEST_DATABASE_NAME = "test";
    public static final String TEST_DATABASE_PASSWORD = "test";
    public static final String TEST_DATABASE_USER = "test";

    private static boolean initialized = false;

    public static Connection getConnection() throws SQLException
    {
        return DatabaseUtils.createDatabaseConnection(TEST_DATABASE_NAME, TEST_DATABASE_USER, TEST_DATABASE_PASSWORD);
    }

    public synchronized static void initializeTestDatabase()
    {
        // Only initialize once
        if (initialized)
        {
            // Skipping initialize
            return;
        }

        // Use the yamltodb tool clear and recreate the schema
        try
        {
            // Sometimes the yamltodb tool can have a difficult time making certain changes, so we just clean the entire
            // schema by forcing it to match an empty schema
            runYamlToDB("src/main/resources/empty_schema.yaml");

            // Once the database schema has been cleaned out, we can make it match the schema.yaml file and run tests on
            // the new (empty) database.
            runYamlToDB("src/main/resources/schema.yaml");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Tester.fail("Unable to start yamltodb process");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Mark the DB as now initialized
        initialized = true;
    }

    private static void runYamlToDB(String resourcePath) throws IOException, InterruptedException
    {
        String command = "yamltodb -U " + TEST_DATABASE_USER + " -W " + TEST_DATABASE_NAME + " " + resourcePath + " -u";
        Process process = Runtime.getRuntime().exec(command);
        // Pipe the password to the process
        process.getOutputStream().write((TEST_DATABASE_PASSWORD + "\n").getBytes());
        process.getOutputStream().flush();
        // Wait for and test exit status
        int exitCode = process.waitFor();
        Tester.equal("Exit code should be 0: ", exitCode, 0);

    }
}
