package io.vivarium.net.common;

import java.util.UUID;

import io.vivarium.serialization.FileIO;
import io.vivarium.util.Version;

public class Pledge extends Message
{
    public final static String TYPE = "PLEDGE";

    public final UUID workerID;
    public final Version version = Version.CURRENT_VERSION;
    public final int fileformat = FileIO.FILE_FORMAT_VERSION;
    public final int slots;
    public final int throughput;

    public Pledge(UUID workerID)
    {
        this.workerID = workerID;
        this.slots = 1;
        this.throughput = 10;
    }

    public Pledge(UUID workerID, int slots, int throughput)
    {
        this.workerID = workerID;
        this.slots = slots;
        this.throughput = throughput;
    }

    @Override
    protected String getType()
    {
        return TYPE;
    }
}