/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.brain.Brain;
import io.vivarium.db.DatabaseUtils;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class Resource
{
    // Table name
    private static final String TABLE_NAME = "resources";
    // Column names
    private static final String ID = "id";
    private static final String DATA = "data";
    private static final String FILE_FORMAT_VERSION = "file_format_version";
    private static final String WORLD_COUNT = "world_count";
    private static final String BLUEPRINT_COUNT = "blueprint_count";
    private static final String SPECIES_COUNT = "species_count";
    private static final String CREATURE_COUNT = "creature_count";
    private static final String BRAIN_COUNT = "brain_count";

    // relation data
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
        String queryString = "SELECT * FROM resources WHERE id = '" + resourceID.toString() + "'";
        System.out.println(queryString);
        ResultSet resourceResultSet = queryStatement.executeQuery(queryString);
        while (resourceResultSet.next())
        {
            String jsonData = resourceResultSet.getString(DATA);
            Integer version = (Integer) resourceResultSet.getObject(FILE_FORMAT_VERSION);
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
        Map<String, Object> resourceRelation = new HashMap<String, Object>();
        resourceRelation.put(ID, resourceID);
        resourceRelation.put(DATA, jsonData.isPresent() ? jsonData.get() : null);
        resourceRelation.put(FILE_FORMAT_VERSION, fileFormatVersion.isPresent() ? fileFormatVersion.get() : null);
        resourceRelation.put(WORLD_COUNT, null);
        resourceRelation.put(BLUEPRINT_COUNT, null);
        resourceRelation.put(SPECIES_COUNT, null);
        resourceRelation.put(CREATURE_COUNT, null);
        resourceRelation.put(BRAIN_COUNT, null);
        if (jsonData.isPresent())
        {
            VivariumObjectCollection collection = JSONConverter.jsonStringToSerializerCollection(jsonData.get());
            resourceRelation.put(WORLD_COUNT, collection.getAll(World.class).size());
            resourceRelation.put(BLUEPRINT_COUNT, collection.getAll(Blueprint.class).size());
            resourceRelation.put(SPECIES_COUNT, collection.getAll(Species.class).size());
            resourceRelation.put(CREATURE_COUNT, collection.getAll(Creature.class).size());
            resourceRelation.put(BRAIN_COUNT, collection.getAll(Brain.class).size());
        }
        DatabaseUtils.upsert(connection, TABLE_NAME, resourceRelation, getPrimaryKeys());
    }

    private List<String> getPrimaryKeys()
    {
        List<String> primaryKeys = new LinkedList<String>();
        primaryKeys.add(ID);
        return primaryKeys;
    }
}
