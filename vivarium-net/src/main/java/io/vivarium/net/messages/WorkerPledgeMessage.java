/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import java.util.Arrays;

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
    public WorkerPledgeMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID,
            @JsonProperty("workerID") @JsonSerialize(using = UUIDSerializer.class) UUID workerID,
            @JsonProperty("active") boolean active,
            @JsonProperty("codeVersion") @JsonSerialize(using = VersionSerializer.class) Version codeVersion,
            @JsonProperty("fileFormatVersion") int fileFormatVersion, @JsonProperty("throughputs") long[] throughputs)
    {
        super(messageID);

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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result + ((codeVersion == null) ? 0 : codeVersion.hashCode());
        result = prime * result + fileFormatVersion;
        result = prime * result + Arrays.hashCode(throughputs);
        result = prime * result + ((workerID == null) ? 0 : workerID.hashCode());
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
        WorkerPledgeMessage other = (WorkerPledgeMessage) obj;
        if (active != other.active)
        {
            return false;
        }
        if (codeVersion == null)
        {
            if (other.codeVersion != null)
            {
                return false;
            }
        }
        else if (!codeVersion.equals(other.codeVersion))
        {
            return false;
        }
        if (fileFormatVersion != other.fileFormatVersion)
        {
            return false;
        }
        if (!Arrays.equals(throughputs, other.throughputs))
        {
            return false;
        }
        if (workerID == null)
        {
            if (other.workerID != null)
            {
                return false;
            }
        }
        else if (!workerID.equals(other.workerID))
        {
            return false;
        }
        return true;
    }
}