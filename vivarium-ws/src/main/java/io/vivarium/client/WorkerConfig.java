/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.serialization.FileIO;
import io.vivarium.util.UUID;
import io.vivarium.util.UserFacingError;

public class WorkerConfig
{
    public static final String DEFAULT_PATH = "worker_config.json";
    public static final int[] DEFAULT_THROUGHPUTS = { 20_000_000, 35_000_000, 50_000_000, 60_500_000, 70_000_000,
            80_000_000, 85_000_000, 86_000_000, 87_000_000, 88_000_000, 89_000_000, 90_000_000 };
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public final UUID workerID;
    public final int[] throughputs;

    private WorkerConfig()
    {
        workerID = null;
        throughputs = null;
    }

    private WorkerConfig(UUID workerID, int[] throughputs)
    {
        Preconditions.checkNotNull(workerID);
        Preconditions.checkNotNull(throughputs);
        this.workerID = workerID;
        this.throughputs = throughputs;
    }

    public static WorkerConfig loadWorkerConfig(File file, boolean generateIfNotFound)
    {
        Preconditions.checkNotNull(file);
        try
        {
            if (file.exists())
            {
                ObjectMapper mapper = new ObjectMapper();
                String configData = FileIO.loadFileToString(file);
                WorkerConfig loadedConfig = mapper.readValue(configData, WorkerConfig.class);
                return loadedConfig;
            }
            else if (generateIfNotFound)
            {
                try
                {
                    UUID machineUUID = getMachineUUID();
                    WorkerConfig generatedConfig = new WorkerConfig(machineUUID, DEFAULT_THROUGHPUTS);
                    generatedConfig.persistWorkerConfig(file);
                    return generatedConfig;
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                    throw new UserFacingError(
                            "Trying to generate the worker configuration failed. This is only known to work on ubuntu systems at this time.");

                }
            }
            else
            {
                throw new UserFacingError("The worker config file " + file.getAbsolutePath()
                        + " can't be found and worker config autogeneration has not been enabled.");
            }
        }
        catch (JsonParseException | JsonMappingException e)
        {
            e.printStackTrace();
            throw new UserFacingError("Unable to read parse file " + file.getAbsolutePath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static UUID getMachineUUID() throws FileNotFoundException
    {
        String uuidString = FileIO.loadFileToString(new File("/var/lib/dbus/machine-id"));
        return UUID.fromString(uuidString);
    }

    private void persistWorkerConfig(File file)
    {
        Preconditions.checkNotNull(file);
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String dataString = mapper.writeValueAsString(this);
            FileIO.saveStringToFile(dataString, file);
        }
        catch (IOException e)
        {
            throw new UserFacingError("The worker config file could not be written to: " + file.getAbsolutePath());
        }
    }
}
