/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.brain.Brain;
import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.Inequality;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;

public class ResourceModel extends PersistenceModel
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

    public ResourceModel(UUID resourceID, String jsonData, Integer fileFormatVersion)
    {
        Preconditions.checkNotNull(resourceID, "resourceID cannot be null");
        this.resourceID = resourceID;
        this.jsonData = jsonData != null ? Optional.of(jsonData) : Optional.empty();
        this.fileFormatVersion = fileFormatVersion != null ? Optional.of(fileFormatVersion) : Optional.empty();
    }

    static Optional<ResourceModel> getFromDatabase(Connection connection, UUID resourceID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, TABLE_NAME,
                Optional.of(Inequality.equalTo(ID, resourceID)));
        if (relations.size() == 1)
        {
            Map<String, Object> relation = relations.get(0);
            ResourceModel resource = new ResourceModel(UUID.fromString(relation.get(ID).toString()),
                    relation.get(DATA).toString(), (Integer) relation.get(FILE_FORMAT_VERSION));
            return Optional.of(resource);
        }
        else if (relations.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            throw new IllegalStateException("Select of Resource returned multiple objects");
        }
    }

    @Override
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
        connection.commit();
    }

    private List<String> getPrimaryKeys()
    {
        List<String> primaryKeys = new LinkedList<String>();
        primaryKeys.add(ID);
        return primaryKeys;
    }
}
