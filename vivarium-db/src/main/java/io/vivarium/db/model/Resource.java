package io.vivarium.db.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.brain.Brain;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class Resource
{
    public final UUID resourceID;
    public final Optional<String> jsonData;
    public final Optional<Integer> fileFormatVersion;

    private Resource(UUID resourceID, String jsonData, Integer fileFormatVersion)
    {
        this.resourceID = resourceID;
        this.jsonData = Optional.of(jsonData);
        this.fileFormatVersion = Optional.of(fileFormatVersion);
    }

    public static Optional<Resource> getFromDatabase(Connection connection, UUID resourceID) throws SQLException
    {
        Statement queryStatement = connection.createStatement();
        // queryStatement.setObject(1, resourceID.toString());
        // System.out.println(queryStatement);
        System.out.println("Hello");
        String queryString = "SELECT * FROM resources WHERE id = '" + resourceID.toString() + "'";
        System.out.println(queryString);
        ResultSet resourceResultSet = queryStatement.executeQuery(queryString);
        System.out.println("Queired");
        while (resourceResultSet.next())
        {
            String jsonData = resourceResultSet.getString("data");
            Integer version = (Integer) resourceResultSet.getObject("file_format_version");
            return Optional.of((new Resource(resourceID, jsonData, version)));
        }
        return Optional.empty();
    }

    public static Resource create(UUID resourceID, String jsonData)
    {
        return new Resource(resourceID, jsonData, Version.FILE_FORMAT_VERSION);
    }

    public void persistToDatabase(Connection connection) throws SQLException
    {
        Integer worldCount = null;
        Integer blueprintCount = null;
        Integer speciesCount = null;
        Integer creatureCount = null;
        Integer brainCount = null;
        if (jsonData.isPresent())
        {
            VivariumObjectCollection collection = JSONConverter.jsonStringToSerializerCollection(jsonData.get());
            worldCount = collection.getAll(World.class).size();
            blueprintCount = collection.getAll(Blueprint.class).size();
            speciesCount = collection.getAll(Species.class).size();
            creatureCount = collection.getAll(Creature.class).size();
            brainCount = collection.getAll(Brain.class).size();
            System.out.println("worldCount   " + worldCount);
        }
        else
        {
            System.err.println("Empty rows?");
        }
        Statement insertStatement = connection.createStatement();
        String jsonDataForSQL = (jsonData.isPresent() ? '\'' + jsonData.get() + '\'' : "null");
        String updateString = String.format(
                "UPDATE resources SET data=%s, file_format_version=%s, world_count=%s, blueprint_count=%s, species_count=%s, creature_count=%s, brain_count=%s  WHERE id='%s'",
                jsonDataForSQL, fileFormatVersion.get(), worldCount, blueprintCount, speciesCount, creatureCount,
                brainCount, resourceID.toString());
        String insertString = String.format(
                "INSERT INTO resources (id, data, file_format_version, world_count, blueprint_count, species_count, creature_count, brain_count) SELECT  '%s', %s, %s, %s, %s, %s, %s, %s WHERE NOT EXISTS (SELECT 1 FROM resources WHERE id='%s');",
                resourceID.toString(), jsonDataForSQL, fileFormatVersion.get(), worldCount, blueprintCount,
                speciesCount, creatureCount, brainCount, resourceID.toString());
        insertStatement.execute(updateString);
        insertStatement.execute(insertString);
    }
}
