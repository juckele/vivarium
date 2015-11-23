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

public class Pledge extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public UUID workerID;
    @JsonSerialize(using = VersionSerializer.class)
    @JsonDeserialize(using = VersionDeserializer.class)
    public Version version = Version.CURRENT_VERSION;
    public int fileformat = Version.FILE_FORMAT_VERSION;
    public int[] throughput;

    @SuppressWarnings("unused") // Used by Jackson
    private Pledge()
    {
        workerID = null;
        version = null;
        fileformat = -1;
        throughput = null;
    }

    public Pledge(UUID workerID)
    {
        this.workerID = workerID;
        throughput = new int[] { 10_000_000, 15_000_000, 20_000_000, 22_500_000, 25_000_000, 27_500_000, 30_000_000 };
    }

    public Pledge(UUID workerID, int slots, int[] throughput)
    {
        this.workerID = workerID;
        this.throughput = throughput;
    }
}