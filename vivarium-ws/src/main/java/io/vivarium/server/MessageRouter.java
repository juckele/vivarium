/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.net.jobs.CreateWorldJob;
import io.vivarium.net.jobs.SimulationJob;
import io.vivarium.net.messages.CreateJobMessage;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.RequestResourceMessage;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.net.messages.WorkerPledgeMessage;
import io.vivarium.persistence.CreateWorldJobModel;
import io.vivarium.persistence.JobModel;
import io.vivarium.persistence.JobStatus;
import io.vivarium.persistence.PersistenceModule;
import io.vivarium.persistence.ResourceModel;
import io.vivarium.persistence.RunSimulationJobModel;
import io.vivarium.persistence.WorkerModel;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class MessageRouter implements StartableStoppable
{
    private final PersistenceModule _persistenceModule;
    private final ClientConnectionManager _connectionManager;
    private final VoidFunctionScheduler _enforcerScheduler;
    private final ObjectMapper mapper = new ObjectMapper();

    public MessageRouter(PersistenceModule persistenceModule, ClientConnectionManager connectionManager,
            VoidFunctionScheduler enforcerScheduler)
    {
        _persistenceModule = persistenceModule;
        _connectionManager = connectionManager;
        _enforcerScheduler = enforcerScheduler;
    }

    @Override
    public void start()
    {
        _connectionManager.start();
        _enforcerScheduler.start();
    }

    @Override
    public void stop()
    {
        _enforcerScheduler.stop();
        _connectionManager.stop();
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        System.out.println("SERVER: Web Socket Connection Opened. " + conn + " ~ " + handshake);
    }

    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        System.out.println(
                "SERVER: Web Socket Connection closed. " + conn + " ~ " + code + " # " + reason + " & " + remote);
    }

    public void onMessage(WebSocket conn, String message)
    {
        try
        {
            Message untypedMessage = mapper.readValue(message, Message.class);
            if (untypedMessage instanceof WorkerPledgeMessage)
            {
                acceptPledge(conn, (WorkerPledgeMessage) untypedMessage);
            }
            else if (untypedMessage instanceof SendResourceMessage)
            {
                acceptResource(conn, (SendResourceMessage) untypedMessage);
            }
            else if (untypedMessage instanceof RequestResourceMessage)
            {
                handleRequestForResource(conn, (RequestResourceMessage) untypedMessage);
            }
            else if (untypedMessage instanceof CreateJobMessage)
            {
                System.out.println("CreateJobMessage: " + message);
                acceptJob(conn, (CreateJobMessage) untypedMessage);
            }
            else
            {
                System.err.println("SERVER: Unhandled message of type " + untypedMessage.getClass().getSimpleName());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(
                "SERVER: Web Socket Message . " + conn + " ~ " + message.substring(0, Math.min(message.length(), 200)));
    }

    private synchronized void acceptPledge(WebSocket webSocket, WorkerPledgeMessage pledge)
    {
        WorkerModel worker = new WorkerModel(pledge.workerID, pledge.throughputs, pledge.active, new Date(),
                pledge.fileFormatVersion, pledge.codeVersion);
        _persistenceModule.persist(worker);
        _connectionManager.registerWorker(pledge.workerID, webSocket);
    }

    private void acceptResource(WebSocket webSocket, SendResourceMessage sendResourceMessage)
    {
        String dataString = sendResourceMessage.dataString;
        String jsonString;
        if (sendResourceMessage.resourceFormat == ResourceFormat.JSON)
        {
            jsonString = sendResourceMessage.dataString;
        }
        else if (sendResourceMessage.resourceFormat == ResourceFormat.GWT_STREAM)
        {
            VivariumObjectCollection collection = (VivariumObjectCollection) Streamer.get().fromString(dataString);
            jsonString = JSONConverter.serializerToJSONString(collection);
        }
        else
        {
            throw new IllegalStateException("Unexpected resource format " + sendResourceMessage.resourceFormat);
        }
        ResourceModel resource = new ResourceModel(sendResourceMessage.resourceID, jsonString,
                Version.FILE_FORMAT_VERSION);
        _persistenceModule.persist(resource);
    }

    private void acceptJob(WebSocket conn, CreateJobMessage createJobMessage)
    {
        JobModel job;
        if (createJobMessage.job instanceof SimulationJob)
        {
            SimulationJob simulationJob = (SimulationJob) createJobMessage.job;
            job = new RunSimulationJobModel(simulationJob.jobID, JobStatus.BLOCKED, (short) 0, null, null, null,
                    simulationJob.endTick, simulationJob.inputResources, simulationJob.outputResources,
                    simulationJob.dependencies);
        }
        else if (createJobMessage.job instanceof CreateWorldJob)
        {
            CreateWorldJob createWorldJob = (CreateWorldJob) createJobMessage.job;
            job = new CreateWorldJobModel(createWorldJob.jobID, JobStatus.BLOCKED, (short) 0, null, null, null,
                    createWorldJob.inputResources, createWorldJob.outputResources, createWorldJob.dependencies);
        }
        else
        {
            throw new IllegalStateException("Unexpected job type " + createJobMessage.job.getClass().getSimpleName());
        }
        _persistenceModule.persist(job);
    }

    private void handleRequestForResource(WebSocket webSocket, RequestResourceMessage requestResourceMessage)
            throws IOException
    {
        UUID resourceID = requestResourceMessage.resourceID;
        Optional<ResourceModel> resource = _persistenceModule.fetch(resourceID, ResourceModel.class);
        if (resource.isPresent() && resource.get().jsonData.isPresent())
        {
            ResourceFormat resourceFormat = requestResourceMessage.resourceFormat;
            String jsonString = resource.get().jsonData.get();
            String dataString = null;
            if (resourceFormat == ResourceFormat.JSON)
            {
                dataString = jsonString;
            }
            else if (resourceFormat == ResourceFormat.GWT_STREAM)
            {
                VivariumObjectCollection collection = JSONConverter.jsonStringToSerializerCollection(jsonString);
                dataString = Streamer.get().toString(collection);
            }
            else
            {
                throw new IllegalStateException("Unexpected resource format " + resourceFormat);
            }
            SendResourceMessage response = new SendResourceMessage(requestResourceMessage.resourceID, dataString,
                    resourceFormat);
            webSocket.send(mapper.writeValueAsString(response));
        }
    }

    public void onError(WebSocket conn, Exception ex)
    {
        System.out.println("SERVER: Web Socket Error . " + conn + " ~ " + ex);
        ex.printStackTrace();
    }
}
