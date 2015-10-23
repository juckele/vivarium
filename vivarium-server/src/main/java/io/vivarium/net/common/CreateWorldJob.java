package io.vivarium.net.common;

import java.util.List;
import java.util.UUID;

public class CreateWorldJob extends Job
{
    public final UUID sourceDocumentID;
    public final UUID outputDocumentID;

    public CreateWorldJob(List<Job> dependencies, UUID sourceDocumentID, UUID outputDocumentID)
    {
        super(dependencies);
        this.sourceDocumentID = sourceDocumentID;
        this.outputDocumentID = outputDocumentID;
    }

    @Override
    protected JobType getType()
    {
        return JobType.CREATE_WORLD;
    }
}