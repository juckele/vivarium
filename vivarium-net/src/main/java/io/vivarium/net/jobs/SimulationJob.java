package io.vivarium.net.jobs;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class SimulationJob extends Job
{
    private final long _endTick;

    @JsonCreator
    public SimulationJob(@JsonProperty("type") JobType jobType,
            @JsonProperty("jobID") @JsonSerialize(using = UUIDSerializer.class) UUID jobID,
            @JsonProperty("inputResources") Collection<UUID> inputResources,
            @JsonProperty("outputResources") Collection<UUID> outputResources,
            @JsonProperty("dependencies") Collection<UUID> dependencies, @JsonProperty("endTick") long endTick)
    {
        super(jobType, jobID, inputResources, outputResources, dependencies);
        Preconditions.checkArgument(jobType == JobType.RUN_SIMULATION);
        this._endTick = endTick;
    }

    public SimulationJob(Collection<UUID> inputResources, Collection<UUID> outputResources,
            Collection<UUID> dependencies, long endTick)
    {
        this(JobType.RUN_SIMULATION, UUID.randomUUID(), inputResources, outputResources, dependencies, endTick);
    }

    public long getEndTick()
    {
        return _endTick;
    }
}
