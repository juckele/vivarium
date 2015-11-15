/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.common.messages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.net.VersionDeserializer;
import io.vivarium.net.VersionSerializer;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class Pledge extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public UUID workerID;
    @JsonSerialize(using = VersionSerializer.class)
    @JsonDeserialize(using = VersionDeserializer.class)
    public Version version = Version.CURRENT_VERSION;
    public int fileformat = Version.FILE_FORMAT_VERSION;
    public int slots;
    public int throughput;

    @SuppressWarnings("unused") // Used by Jackson
    private Pledge()
    {
        workerID = null;
        version = null;
        fileformat = -1;
        slots = -1;
        throughput = -1;
    }

    public Pledge(UUID workerID)
    {
        this.workerID = workerID;
        this.slots = 12;
        this.throughput = 100_000_000;
    }

    public Pledge(UUID workerID, int slots, int throughput)
    {
        this.workerID = workerID;
        this.slots = slots;
        this.throughput = throughput;
    }
}