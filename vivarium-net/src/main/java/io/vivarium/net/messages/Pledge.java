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
    public boolean active;
    @JsonSerialize(using = VersionSerializer.class)
    @JsonDeserialize(using = VersionDeserializer.class)
    public Version codeVersion = Version.CURRENT_VERSION;
    public int fileFormatVersion = Version.FILE_FORMAT_VERSION;
    public int[] throughputs;

    @SuppressWarnings("unused") // Used by Jackson
    private Pledge()
    {
    }

    public Pledge(UUID workerID)
    {
        this.workerID = workerID;
        this.active = true;
        this.codeVersion = Version.CURRENT_VERSION;
        this.fileFormatVersion = Version.FILE_FORMAT_VERSION;
        // Hand waved throughput counts for now that happen to be somewhat close to various machines I own
        this.throughputs = new int[] { 20_000_000, 35_000_000, 50_000_000, 60_500_000, 70_000_000, 80_000_000,
                85_000_000, 86_000_000, 87_000_000, 88_000_000, 89_000_000, 90_000_000 };
    }

    public Pledge(UUID workerID, boolean active, Version codeVersion, int fileFormatVersion, int[] throughputs)
    {
        this.workerID = workerID;
        this.active = active;
        this.codeVersion = codeVersion;
        this.fileFormatVersion = fileFormatVersion;
        this.throughputs = throughputs;
    }
}