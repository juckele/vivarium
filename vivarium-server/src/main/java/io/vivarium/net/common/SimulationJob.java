package io.vivarium.net.common;

import java.util.List;
import java.util.UUID;

public class SimulationJob extends Job
{
    public final UUID sourceDocumentID;
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
