/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

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
        this._inputResources = new ArrayList<UUID>(inputResources);
        this._outputResources = new ArrayList<UUID>(outputResources);
        this._dependencies = new ArrayList<UUID>(dependencies);
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_dependencies == null) ? 0 : _dependencies.hashCode());
        result = prime * result + ((_inputResources == null) ? 0 : _inputResources.hashCode());
        result = prime * result + ((_jobID == null) ? 0 : _jobID.hashCode());
        result = prime * result + ((_outputResources == null) ? 0 : _outputResources.hashCode());
        result = prime * result + ((_type == null) ? 0 : _type.hashCode());
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
        Job other = (Job) obj;
        if (_dependencies == null)
        {
            if (other._dependencies != null)
            {
                return false;
            }
        }
        else if (!_dependencies.equals(other._dependencies))
        {
            return false;
        }
        if (_inputResources == null)
        {
            if (other._inputResources != null)
            {
                return false;
            }
        }
        else if (!_inputResources.equals(other._inputResources))
        {
            return false;
        }
        if (_jobID == null)
        {
            if (other._jobID != null)
            {
                return false;
            }
        }
        else if (!_jobID.equals(other._jobID))
        {
            return false;
        }
        if (_outputResources == null)
        {
            if (other._outputResources != null)
            {
                return false;
            }
        }
        else if (!_outputResources.equals(other._outputResources))
        {
            return false;
        }
        if (_type != other._type)
        {
            return false;
        }
        return true;
    }

}
