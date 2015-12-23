/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.net.VersionDeserializer;
import io.vivarium.net.VersionSerializer;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class WorkerPledgeMessage extends Message
{
    private final UUID workerID;
    private final boolean active;
    private final Version codeVersion;
    private final int fileFormatVersion;
    private final long[] throughputs;

    public WorkerPledgeMessage(UUID workerID, long[] throughputs)
    {
        this(UUID.randomUUID(), workerID, true, Version.CURRENT_VERSION, Version.FILE_FORMAT_VERSION, throughputs);
    }

    @JsonCreator
    public WorkerPledgeMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID,@JsonProperty("workerID") @JsonSerialize(using = UUIDSerializer.class) UUID workerID,
            @JsonProperty("active") boolean active,
            @JsonProperty("codeVersion") @JsonSerialize(using = VersionSerializer.class) Version codeVersion,
            @JsonProperty("fileFormatVersion") int fileFormatVersion, @JsonProperty("throughputs") long[] throughputs)
    {        super(messageID);

        this.workerID = workerID;
        this.active = active;
        this.codeVersion = codeVersion;
        this.fileFormatVersion = fileFormatVersion;
        this.throughputs = throughputs;
    }

    @JsonDeserialize(using = UUIDDeserializer.class)
    public UUID getWorkerID()
    {
        return workerID;
    }

    public boolean isActive()
    {
        return active;
    }

    @JsonDeserialize(using = VersionDeserializer.class)
    public Version getCodeVersion()
    {
        return codeVersion;
    }

    public int getFileFormatVersion()
    {
        return fileFormatVersion;
    }

    public long[] getThroughputs()
    {
        return throughputs;
    }
}