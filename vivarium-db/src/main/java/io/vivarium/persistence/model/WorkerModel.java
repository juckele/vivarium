/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.persistence.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.Inequality;
import io.vivarium.persistence.PersistenceModel;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class WorkerModel extends PersistenceModel
{
    // Table name
    private static final String TABLE_NAME = "workers";
    // Column names
    private static final String ID = "id";
    private static final String THROUGHPUTS = "throughputs";
    private static final String IS_ACTIVE = "is_active";
    private static final String LAST_ACTIVITY = "last_activity";
    private static final String CODE_VERSION = "code_version";
    private static final String FILE_FORMAT_VERSION = "file_format_version";

    // relation data
    public final UUID workerID;
    public final int[] throughputs;
    public final boolean isActive;
    public final Date lastActivity;
    public final int fileFormatVersion;
    public final Version codeVersion;
    // insert into workers (id, throughputs, is_active, last_activity, file_format_version, code_version) select
    // 'ec1bb1e7-d471-363e-7724-bf995021f543', '{100, 150, 200}', true, now(), 1, '{0,3,2}';

    public WorkerModel(UUID workerID, int[] throughputs, boolean isActive, Date lastActivity, int fileFormatVersion,
            Version codeVersion)
    {
        Preconditions.checkNotNull(workerID, "workerID cannot be null");
        Preconditions.checkNotNull(throughputs, "throughputs cannot be null");
        Preconditions.checkNotNull(lastActivity, "lastActivity cannot be null");
        Preconditions.checkNotNull(codeVersion, "codeVersion cannot be null");
        this.workerID = workerID;
        this.throughputs = throughputs;
        this.isActive = isActive;
        this.lastActivity = lastActivity;
        this.fileFormatVersion = fileFormatVersion;
        this.codeVersion = codeVersion;
    }

    public static Optional<WorkerModel> getFromDatabase(Connection connection, UUID resourceID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, TABLE_NAME,
                Optional.of(Inequality.equalTo(ID, resourceID)));
        if (relations.size() == 1)
        {
            Map<String, Object> relation = relations.get(0);
            UUID id = UUID.fromString(relation.get(ID).toString());
            int[] throughputs = (int[]) relation.get(THROUGHPUTS);
            boolean isActive = (Boolean) relation.get(IS_ACTIVE);
            Date lastActivity = (Date) relation.get(LAST_ACTIVITY);
            int fileFormatVersion = (Integer) relation.get(FILE_FORMAT_VERSION);
            Version codeVersion = new Version((int[]) relation.get(CODE_VERSION));
            WorkerModel resource = new WorkerModel(id, throughputs, isActive, lastActivity, fileFormatVersion,
                    codeVersion);
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
        resourceRelation.put(ID, workerID);
        resourceRelation.put(THROUGHPUTS, throughputs);
        resourceRelation.put(IS_ACTIVE, isActive);
        resourceRelation.put(LAST_ACTIVITY, lastActivity);
        resourceRelation.put(CODE_VERSION, codeVersion);
        resourceRelation.put(FILE_FORMAT_VERSION, fileFormatVersion);
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
