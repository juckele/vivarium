/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.common.messages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class Pledge extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public UUID workerID;
    public final Version version = Version.CURRENT_VERSION;
    public final int fileformat = Version.FILE_FORMAT_VERSION;
    public final int slots;
    public final int throughput;

    @SuppressWarnings("unused") // Used by Jackson
    private Pledge()
    {
        workerID = UUID.randomUUID();
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