/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

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
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public UUID workerID;
    public boolean active;
    @JsonSerialize(using = VersionSerializer.class)
    @JsonDeserialize(using = VersionDeserializer.class)
    public Version codeVersion = Version.CURRENT_VERSION;
    public int fileFormatVersion = Version.FILE_FORMAT_VERSION;
    public int[] throughputs;

    @SuppressWarnings("unused") // Used by Jackson
    private WorkerPledgeMessage()
    {
    }

    public WorkerPledgeMessage(UUID workerID, int[] throughputs)
    {
        this(workerID, true, Version.CURRENT_VERSION, Version.FILE_FORMAT_VERSION, throughputs);
    }

    public WorkerPledgeMessage(UUID workerID, boolean active, Version codeVersion, int fileFormatVersion, int[] throughputs)
    {
        this.workerID = workerID;
        this.active = active;
        this.codeVersion = codeVersion;
        this.fileFormatVersion = fileFormatVersion;
        this.throughputs = throughputs;
    }
}