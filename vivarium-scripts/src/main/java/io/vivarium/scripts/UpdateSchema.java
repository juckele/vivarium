package io.vivarium.scripts;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.schema.Schema;
import io.vivarium.serialization.FileIO;

public class UpdateSchema extends CommonsScript
{
    private static final String SCHEMA_FILE = "schema";
    private static final String DATABASE_NAME = "database";
    private static final String DEFAULT_DB_NAME = "vivarium";
    private static final String DROP_MISSING = "drop";

    public UpdateSchema(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("i").required(true).longOpt(SCHEMA_FILE).hasArg(true).argName("FILE_NAME")
                .desc("The .json file to use as a schema description.").build());
        options.add(Option.builder("n").required(false).longOpt(DATABASE_NAME).hasArg(true).argName("DB_NAME")
                .desc("The named database to update the schema of. Defaults to " + DEFAULT_DB_NAME + ".").build());
        options.add(Option.builder("d").required(false).longOpt(DROP_MISSING).hasArg(false)
                .desc("Drop tables or columns if they've been removed in the code").build());
        return options;
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    @Override
    protected String getUsageHeader()
    {
        return "Update the database schema.";
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Load schema file
        String schemaFile = commandLine.getOptionValue(SCHEMA_FILE);
        String jsonSchemaData = FileIO.loadFileToString(schemaFile);

        // Get options
        String databaseName;
        if (commandLine.hasOption(DATABASE_NAME))
        {
            databaseName = commandLine.getOptionValue(DATABASE_NAME);
        }
        else
        {
            databaseName = DEFAULT_DB_NAME;
        }

        boolean dropMissingElements = false;
        if (commandLine.hasOption(DROP_MISSING))
        {
            dropMissingElements = true;
        }

        // Run the update
        run(databaseName, jsonSchemaData, dropMissingElements);
    }

    public void run(String databaseName, String jsonSchemaData, boolean dropMissingElements)
    {
        try (Connection connection = DatabaseUtils.createDatabaseConnection(databaseName))
        {
            ObjectMapper mapper = new ObjectMapper();

            Schema testSchema = new Schema(true);
            System.out.println(testSchema);
            String testJsonString = mapper.writeValueAsString(testSchema);
            System.out.println(testJsonString);

            Schema loadedSchema = mapper.readValue(jsonSchemaData, Schema.class);
            System.out.println(loadedSchema);
            String loadedJsonString = mapper.writeValueAsString(testSchema);
            System.out.println(loadedJsonString);

            DatabaseUtils.applySchema(connection, loadedSchema, dropMissingElements);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        new UpdateSchema(args);
    }

}
