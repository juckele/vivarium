package io.vivarium.net.common.jobs;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class SimulationJob extends Job
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public final UUID sourceDocumentID;
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public final UUID outputDocumentID;
    public final long endTick;

    public SimulationJob(List<Job> dependencies, UUID sourceDocumentID, UUID outputDocumentID, long endTick)
    {
        super(dependencies);
        this.sourceDocumentID = sourceDocumentID;
        this.outputDocumentID = outputDocumentID;
        this.endTick = endTick;
    }

    @Override
    protected JobType getType()
    {
        return JobType.RUN_SIMULATION;
    }
}
