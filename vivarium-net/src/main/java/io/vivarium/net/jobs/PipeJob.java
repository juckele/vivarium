package io.vivarium.net.jobs;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public abstract class PipeJob extends Job
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public final UUID sourceDocumentID;
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public final UUID outputDocumentID;

    protected PipeJob()
    {
        super();
        sourceDocumentID = null;
        outputDocumentID = null;
    }

    public PipeJob(List<UUID> dependencies, UUID sourceDocumentID, UUID outputDocumentID)
    {
        super(dependencies);
        this.sourceDocumentID = sourceDocumentID;
        this.outputDocumentID = outputDocumentID;
    }

}
