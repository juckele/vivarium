/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db.model;

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
import io.vivarium.util.UUID;

public abstract class JobModel implements DatabaseObjectModel
{
    // Table name
    private static final String TABLE_NAME = "jobs";
    // Column names
    protected static final String ID = "id";
    protected static final String JOB_TYPE = "job_type";
    protected static final String JOB_STATUS = "status";
    protected static final String PRIORITY = "priority";
    protected static final String CHECKED_OUT_BY = "checked_out_by";
    protected static final String CHECKED_OUT_TIME = "checked_out_time";
    protected static final String COMEPLTED_TIME = "completed_time";
    // Junction table for dependencies
    private static final String DEPENDENCIES_TABLE_NAME = "job_dependencies";
    protected static final String DEPENDENCIES_FROM_ID = "job_id";
    protected static final String DEPENDENCIES_TO_ID = "requires_job_id";

    // relation data

    public final UUID jobID;
    public final JobType type;
    public final JobStatus status;
    public final short priority;
    public final Optional<UUID> checkedOutByWorkerID;
    public final Optional<Date> checkedOutTime;
    public final Optional<Date> completedTime;
    public final List<UUID> jobDependencies;

    public JobModel(UUID jobID, JobType type, JobStatus status, short priority, UUID checkedOutByWorkerID,
            Date checkedOutTime, Date completedTime, List<UUID> jobDependencies)
    {
        Preconditions.checkNotNull(jobID, "jobID cannot be null");
        Preconditions.checkNotNull(type, "type cannot be null");
        Preconditions.checkNotNull(status, "staus cannot be null");
        Preconditions.checkNotNull(jobDependencies, "jobDependencies cannot be null");
        this.jobID = jobID;
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.checkedOutByWorkerID = checkedOutByWorkerID != null ? Optional.of(checkedOutByWorkerID) : Optional.empty();
        this.checkedOutTime = checkedOutTime != null ? Optional.of(checkedOutTime) : Optional.empty();
        this.completedTime = completedTime != null ? Optional.of(completedTime) : Optional.empty();
        this.jobDependencies = new LinkedList<>(jobDependencies);
    }

    public JobModel(Map<String, Object> relation, List<UUID> jobDependencies)
    {
        Preconditions.checkNotNull(relation.get(ID), "jobID cannot be null");
        Preconditions.checkNotNull(relation.get(JOB_TYPE), "type cannot be null");
        Preconditions.checkNotNull(relation.get(JOB_STATUS), "staus cannot be null");
        Preconditions.checkNotNull(jobDependencies, "jobDependencies cannot be null");
        this.jobID = UUID.fromString(relation.get(ID).toString());
        this.type = JobType.valueOf(relation.get(JOB_TYPE).toString());
        this.status = JobStatus.valueOf(relation.get(JOB_STATUS).toString());
        this.priority = (Short) relation.get(PRIORITY);
        this.checkedOutByWorkerID = null;
        this.checkedOutTime = null;
        this.completedTime = null;
        this.jobDependencies = new LinkedList<>(jobDependencies);
    }

    @Override
    public void persistToDatabase(Connection connection) throws SQLException
    {
        Map<String, Object> jobRelation = getRelationModel();
        List<Map<String, Object>> dependencyRelations = new LinkedList<>();
        for (UUID jobDependencyID : jobDependencies)
        {
            Map<String, Object> dependencyRelation = new HashMap<>();
            dependencyRelation.put(DEPENDENCIES_FROM_ID, this.jobID);
            dependencyRelation.put(DEPENDENCIES_TO_ID, jobDependencyID);
            dependencyRelations.add(dependencyRelation);
        }
        DatabaseUtils.upsert(connection, getTableName(), jobRelation, getPrimaryKeys());
        DatabaseUtils.updateJunctionTable(connection, DEPENDENCIES_TABLE_NAME, dependencyRelations,
                DEPENDENCIES_FROM_ID, this.jobID);

    }

    private List<String> getPrimaryKeys()
    {
        List<String> primaryKeys = new LinkedList<String>();
        primaryKeys.add(ID);
        return primaryKeys;
    }

    protected Map<String, Object> getRelationModel()
    {
        Map<String, Object> relation = new HashMap<>();
        relation.put(ID, jobID);
        relation.put(JOB_TYPE, type);
        relation.put(JOB_STATUS, status);
        relation.put(PRIORITY, priority);
        relation.put(CHECKED_OUT_BY, checkedOutByWorkerID);
        relation.put(CHECKED_OUT_TIME, checkedOutTime);
        relation.put(COMEPLTED_TIME, completedTime);
        return relation;
    }

    public static Optional<JobModel> getFromDatabase(Connection connection, UUID jobID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, TABLE_NAME,
                Optional.of(Inequality.equalTo(ID, jobID)));
        if (relations.size() == 1)
        {
            // find the depencies
            List<UUID> dependencies = getDepdendencies(connection, jobID);

            // Determine the type
            Map<String, Object> relation = relations.get(0);
            JobModel job;
            JobType type = JobType.valueOf(relation.get(JOB_TYPE).toString());

            // Construct the sub-type
            if (type == JobType.RUN_SIMULATION)
            {
                job = new RunSimulationJobModel(relation, dependencies);
            }
            else if (type == JobType.CREATE_WORLD)
            {
                job = new CreateWorldJobModel(relation, dependencies);
            }
            else
            {
                throw new IllegalStateException("Unable to get job type " + type + " from database.");
            }
            return Optional.of(job);
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

    private static List<UUID> getDepdendencies(Connection connection, UUID jobID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, DEPENDENCIES_TABLE_NAME,
                Optional.of(Inequality.equalTo(DEPENDENCIES_FROM_ID, jobID)));
        List<UUID> dependencies = new LinkedList<>();
        for (Map<String, Object> relation : relations)
        {
            dependencies.add(UUID.fromString(relation.get(DEPENDENCIES_TO_ID).toString()));
        }
        return dependencies;
    }
}
