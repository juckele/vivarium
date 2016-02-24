package io.vivarium.net.jobs;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Job
{
    private final JobType _type;
    private final UUID _jobID;
    private final Collection<UUID> _inputResources;
    private final Collection<UUID> _outputResources;
    private final Collection<UUID> _dependencies;

    @JsonCreator

    public Job(@JsonProperty("type") JobType jobType,
            @JsonProperty("jobID") @JsonSerialize(using = UUIDSerializer.class) UUID jobID,
            @JsonProperty("inputResources") Collection<UUID> inputResources,
            @JsonProperty("outputResources") Collection<UUID> outputResources,
            @JsonProperty("dependencies") Collection<UUID> dependencies)
    {
        _type = jobType;
        _jobID = jobID;
        this._inputResources = new ArrayList<>(inputResources);
        this._outputResources = new ArrayList<>(outputResources);
        this._dependencies = new ArrayList<>(dependencies);
    }

    public JobType getType()
    {
        return _type;
    }

    @JsonDeserialize(using = UUIDDeserializer.class)
    public UUID getJobID()
    {
        return _jobID;
    }

    @JsonSerialize(contentUsing = UUIDSerializer.class)
    public Collection<UUID> getInputResources()
    {
        return _inputResources;
    }

    @JsonSerialize(contentUsing = UUIDSerializer.class)
    public Collection<UUID> getOutputResources()
    {
        return _outputResources;
    }

    @JsonSerialize(contentUsing = UUIDSerializer.class)
    public Collection<UUID> getDependencies()
    {
        return _dependencies;
    }
}
