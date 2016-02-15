package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.Relation;
import io.vivarium.util.UUID;

public abstract class JobModel extends PersistenceModel
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
    protected static final String COMPLETED_TIME = "completed_time";
    // Junction table for dependencies
    private static final String DEPENDENCIES_TABLE_NAME = "job_dependencies";
    protected static final String DEPENDENCIES_FROM_ID = "job_id";
    protected static final String DEPENDENCIES_TO_ID = "requires_job_id";
    // Junction tables for resources
    private static final String INPUT_RESOURCES_TABLE_NAME = "job_input_resources";
    private static final String OUTPUT_RESOURCES_TABLE_NAME = "job_output_resources";
    protected static final String RESOURCES_FROM_ID = "job_id";
    protected static final String RESOURCES_TO_ID = "resource_id";
    // Job Property KV table
    private static final String PROPERTY_TABLE_NAME = "job_properties";
    protected static final String PROPERTY_JOB_ID = "job_id";
    protected static final String PROPERTY_KEY = "property_name";
    protected static final String PROPERTY_VALUE = "property_value";

    // relation data
    private final UUID jobID;
    private final JobType type;
    private final JobStatus status;
    private final int priority;
    private final Optional<UUID> checkedOutByWorkerID;
    private final Optional<Timestamp> checkedOutTime;
    private final Optional<Timestamp> completedTime;
    private final Collection<UUID> inputResources;
    private final Collection<UUID> outputResources;
    private final Collection<UUID> jobDependencies;

    public JobModel(UUID jobID, JobType type, JobStatus status, int priority, UUID checkedOutByWorkerID,
            Timestamp checkedOutTime, Timestamp completedTime, Collection<UUID> inputResources,
            Collection<UUID> outputResources, Collection<UUID> jobDependencies)
    {
        Preconditions.checkNotNull(jobID);
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(status);
        Preconditions.checkNotNull(inputResources);
        Preconditions.checkNotNull(outputResources);
        Preconditions.checkNotNull(jobDependencies);

        this.jobID = jobID;
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.checkedOutByWorkerID = checkedOutByWorkerID != null ? Optional.of(checkedOutByWorkerID) : Optional.empty();
        this.checkedOutTime = checkedOutTime != null ? Optional.of(checkedOutTime) : Optional.empty();
        this.completedTime = completedTime != null ? Optional.of(completedTime) : Optional.empty();
        this.inputResources = new LinkedList<>(inputResources);
        this.outputResources = new LinkedList<>(outputResources);
        this.jobDependencies = new LinkedList<>(jobDependencies);
    }

    public JobModel(Map<String, Object> relation, Map<String, String> properties, Collection<UUID> inputResources,
            Collection<UUID> outputResources, Collection<UUID> jobDependencies)
    {
        Preconditions.checkNotNull(properties);
        Preconditions.checkNotNull(relation.get(ID));
        Preconditions.checkNotNull(relation.get(JOB_TYPE));
        Preconditions.checkNotNull(relation.get(JOB_STATUS));
        Preconditions.checkNotNull(inputResources);
        Preconditions.checkNotNull(outputResources);
        Preconditions.checkNotNull(jobDependencies);

        this.jobID = UUID.fromString(relation.get(ID).toString());
        this.type = JobType.valueOf(relation.get(JOB_TYPE).toString());
        this.status = JobStatus.valueOf(relation.get(JOB_STATUS).toString());
        this.priority = Integer.parseInt(relation.get(PRIORITY).toString());
        this.checkedOutByWorkerID = relation.get(CHECKED_OUT_BY) != null
                ? Optional.of((UUID) relation.get(CHECKED_OUT_BY)) : Optional.empty();
        this.checkedOutTime = relation.get(CHECKED_OUT_TIME) != null
                ? Optional.of((Timestamp) relation.get(CHECKED_OUT_TIME)) : Optional.empty();
        this.completedTime = relation.get(COMPLETED_TIME) != null
                ? Optional.of((Timestamp) relation.get(COMPLETED_TIME)) : Optional.empty();
        this.inputResources = new LinkedList<>(inputResources);
        this.outputResources = new LinkedList<>(outputResources);
        this.jobDependencies = new LinkedList<>(jobDependencies);
    }

    @Override
    public void persistToDatabase(Connection connection) throws SQLException
    {
        Map<String, Object> jobRelation = buildRelationalModel();
        List<Map<String, Object>> dependencyRelations = buildDependencyRelations();
        List<Map<String, Object>> inputResourceRelations = buildInputResourceRelations();
        List<Map<String, Object>> outputResourceRelations = buildOutputResourceRelations();
        List<Map<String, Object>> propertyRelations = buildPropertyRelations();
        DatabaseUtils.upsert(connection, TABLE_NAME, jobRelation, getPrimaryKeys());
        DatabaseUtils.updateJunctionTable(connection, DEPENDENCIES_TABLE_NAME, dependencyRelations,
                DEPENDENCIES_FROM_ID, this.jobID);
        DatabaseUtils.updateJunctionTable(connection, INPUT_RESOURCES_TABLE_NAME, inputResourceRelations,
                RESOURCES_FROM_ID, this.jobID);
        DatabaseUtils.updateJunctionTable(connection, OUTPUT_RESOURCES_TABLE_NAME, outputResourceRelations,
                RESOURCES_FROM_ID, this.jobID);
        DatabaseUtils.updateJunctionTable(connection, PROPERTY_TABLE_NAME, propertyRelations, PROPERTY_JOB_ID,
                this.jobID);
        connection.commit();
    }

    private List<String> getPrimaryKeys()
    {
        List<String> primaryKeys = new LinkedList<String>();
        primaryKeys.add(ID);
        return primaryKeys;
    }

    protected Map<String, Object> buildRelationalModel()
    {
        Map<String, Object> relation = new HashMap<>();
        relation.put(ID, jobID);
        relation.put(JOB_TYPE, type);
        relation.put(JOB_STATUS, status);
        relation.put(PRIORITY, priority);
        relation.put(CHECKED_OUT_BY, checkedOutByWorkerID);
        relation.put(CHECKED_OUT_TIME, checkedOutTime);
        relation.put(COMPLETED_TIME, completedTime);
        return relation;
    }

    protected List<Map<String, Object>> buildDependencyRelations()
    {
        List<Map<String, Object>> relations = new LinkedList<>();
        for (UUID jobDependencyID : jobDependencies)
        {
            Map<String, Object> relation = new HashMap<>();
            relation.put(DEPENDENCIES_FROM_ID, this.jobID);
            relation.put(DEPENDENCIES_TO_ID, jobDependencyID);
            relations.add(relation);
        }
        return relations;
    }

    protected List<Map<String, Object>> buildInputResourceRelations()
    {
        List<Map<String, Object>> relations = new LinkedList<>();
        for (UUID resourceID : inputResources)
        {
            Map<String, Object> relation = new HashMap<>();
            relation.put(RESOURCES_FROM_ID, this.jobID);
            relation.put(RESOURCES_TO_ID, resourceID);
            relations.add(relation);
        }
        return relations;
    }

    protected List<Map<String, Object>> buildOutputResourceRelations()
    {
        List<Map<String, Object>> relations = new LinkedList<>();
        for (UUID resourceID : outputResources)
        {
            Map<String, Object> relation = new HashMap<>();
            relation.put(RESOURCES_FROM_ID, this.jobID);
            relation.put(RESOURCES_TO_ID, resourceID);
            relations.add(relation);
        }
        return relations;
    }

    private List<Map<String, Object>> buildPropertyRelations()
    {
        Map<String, String> properties = buildProperties();

        List<Map<String, Object>> relations = new LinkedList<>();
        for (Entry<String, String> entry : properties.entrySet())
        {
            Map<String, Object> relation = new HashMap<>();
            relation.put(PROPERTY_JOB_ID, this.jobID);
            relation.put(PROPERTY_KEY, entry.getKey());
            relation.put(PROPERTY_VALUE, entry.getValue());
            relations.add(relation);
        }
        return relations;
    }

    protected Map<String, String> buildProperties()
    {
        return new HashMap<String, String>();
    }

    static List<JobModel> getFromDatabase(Connection connection, JobStatus status) throws SQLException
    {
        List<JobModel> results = new LinkedList<>();
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, TABLE_NAME,
                Optional.of(Relation.equalTo(JOB_STATUS, status)));
        for (Map<String, Object> relationMap : relations)
        {
            UUID jobID = UUID.fromString(relationMap.get(ID).toString());
            Map<String, String> properties = getPropertiesFromDatabase(connection, jobID);
            List<UUID> inputResources = getInputResourcesFromDatabase(connection, jobID);
            List<UUID> outputResources = getOutputResourcesFromDatabase(connection, jobID);
            List<UUID> dependecies = getDepdendenciesFromDatabase(connection, jobID);
            JobModel job = inflateFromMap(relations.get(0), properties, inputResources, outputResources, dependecies);
            results.add(job);
        }
        return results;
    }

    static Optional<JobModel> getFromDatabase(Connection connection, UUID jobID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, TABLE_NAME,
                Optional.of(Relation.equalTo(ID, jobID)));
        if (relations.size() == 1)
        {
            Map<String, String> properties = getPropertiesFromDatabase(connection, jobID);
            List<UUID> inputResources = getInputResourcesFromDatabase(connection, jobID);
            List<UUID> outputResources = getOutputResourcesFromDatabase(connection, jobID);
            List<UUID> dependecies = getDepdendenciesFromDatabase(connection, jobID);
            JobModel job = inflateFromMap(relations.get(0), properties, inputResources, outputResources, dependecies);
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

    private static JobModel inflateFromMap(Map<String, Object> map, Map<String, String> properties,
            Collection<UUID> inputResources, Collection<UUID> outputResouces, Collection<UUID> dependencies)
    {

        // Determine the type
        JobModel job;
        JobType type = JobType.valueOf(map.get(JOB_TYPE).toString());

        // Construct the sub-type
        if (type == JobType.RUN_SIMULATION)
        {
            job = new RunSimulationJobModel(map, properties, inputResources, outputResouces, dependencies);
        }
        else if (type == JobType.CREATE_WORLD)
        {
            job = new CreateWorldJobModel(map, properties, inputResources, outputResouces, dependencies);
        }
        else
        {
            throw new IllegalStateException("Unable to get job type " + type + " from database.");
        }
        return job;
    }

    private static List<UUID> getDepdendenciesFromDatabase(Connection connection, UUID jobID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, DEPENDENCIES_TABLE_NAME,
                Optional.of(Relation.equalTo(DEPENDENCIES_FROM_ID, jobID)));
        List<UUID> dependencies = new LinkedList<>();
        for (Map<String, Object> relation : relations)
        {
            dependencies.add(UUID.fromString(relation.get(DEPENDENCIES_TO_ID).toString()));
        }
        return dependencies;
    }

    private static List<UUID> getInputResourcesFromDatabase(Connection connection, UUID jobID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, INPUT_RESOURCES_TABLE_NAME,
                Optional.of(Relation.equalTo(RESOURCES_FROM_ID, jobID)));
        List<UUID> resources = new LinkedList<>();
        for (Map<String, Object> relation : relations)
        {
            resources.add(UUID.fromString(relation.get(RESOURCES_TO_ID).toString()));
        }
        return resources;
    }

    private static List<UUID> getOutputResourcesFromDatabase(Connection connection, UUID jobID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, OUTPUT_RESOURCES_TABLE_NAME,
                Optional.of(Relation.equalTo(RESOURCES_FROM_ID, jobID)));
        List<UUID> resources = new LinkedList<>();
        for (Map<String, Object> relation : relations)
        {
            resources.add(UUID.fromString(relation.get(RESOURCES_TO_ID).toString()));
        }
        return resources;
    }

    private static Map<String, String> getPropertiesFromDatabase(Connection connection, UUID jobID) throws SQLException
    {
        List<Map<String, Object>> relations = DatabaseUtils.select(connection, PROPERTY_TABLE_NAME,
                Optional.of(Relation.equalTo(PROPERTY_JOB_ID, jobID)));
        Map<String, String> properties = new HashMap<>();
        for (Map<String, Object> relation : relations)
        {
            properties.put((String) relation.get(PROPERTY_KEY), (String) relation.get(PROPERTY_VALUE));
        }
        return properties;
    }

    public static void updateJobStatuses(Connection connection) throws SQLException
    {
        connection.createStatement().execute(JobSQLStrings.updateStatusString);
        connection.commit();
    }

    public UUID getJobID()
    {
        return jobID;
    }

    public JobType getType()
    {
        return type;
    }

    public JobStatus getStatus()
    {
        return status;
    }

    public int getPriority()
    {
        return priority;
    }

    public Optional<UUID> getCheckedOutByWorkerID()
    {
        return checkedOutByWorkerID;
    }

    public Optional<Timestamp> getCheckedOutTime()
    {
        return checkedOutTime;
    }

    public Optional<Timestamp> getCompletedTime()
    {
        return completedTime;
    }

    public Collection<UUID> getInputResources()
    {
        return inputResources;
    }

    public Collection<UUID> getOutputResources()
    {
        return outputResources;
    }

    public Collection<UUID> getJobDependencies()
    {
        return jobDependencies;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((checkedOutByWorkerID == null) ? 0 : checkedOutByWorkerID.hashCode());
        result = prime * result + ((checkedOutTime == null) ? 0 : checkedOutTime.hashCode());
        result = prime * result + ((completedTime == null) ? 0 : completedTime.hashCode());
        result = prime * result + ((inputResources == null) ? 0 : inputResources.hashCode());
        result = prime * result + ((jobDependencies == null) ? 0 : jobDependencies.hashCode());
        result = prime * result + ((jobID == null) ? 0 : jobID.hashCode());
        result = prime * result + ((outputResources == null) ? 0 : outputResources.hashCode());
        result = prime * result + priority;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        JobModel that = (JobModel) obj;
        if (checkedOutByWorkerID == null)
        {
            if (that.checkedOutByWorkerID != null)
            {
                return false;
            }
        }
        else if (!checkedOutByWorkerID.equals(that.checkedOutByWorkerID))
        {
            return false;
        }
        if (checkedOutTime == null)
        {
            if (that.checkedOutTime != null)
            {
                return false;
            }
        }
        else if (!checkedOutTime.equals(that.checkedOutTime))
        {
            return false;
        }
        if (completedTime == null)
        {
            if (that.completedTime != null)
            {
                return false;
            }
        }
        else if (!completedTime.equals(that.completedTime))
        {
            return false;
        }
        if (inputResources == null)
        {
            if (that.inputResources != null)
            {
                return false;
            }
        }
        else if (!inputResources.equals(that.inputResources))
        {
            return false;
        }
        if (jobDependencies == null)
        {
            if (that.jobDependencies != null)
            {
                return false;
            }
        }
        else if (!jobDependencies.equals(that.jobDependencies))
        {
            return false;
        }
        if (jobID == null)
        {
            if (that.jobID != null)
            {
                return false;
            }
        }
        else if (!jobID.equals(that.jobID))
        {
            return false;
        }
        if (outputResources == null)
        {
            if (that.outputResources != null)
            {
                return false;
            }
        }
        else if (!outputResources.equals(that.outputResources))
        {
            return false;
        }
        if (priority != that.priority)
        {
            return false;
        }
        if (status != that.status)
        {
            return false;
        }
        if (type != that.type)
        {
            return false;
        }
        return true;
    }
}
