/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.model.JobModel;
import io.vivarium.db.model.JobStatus;
import io.vivarium.db.model.ResourceModel;
import io.vivarium.db.model.RunSimulationJobModel;
import io.vivarium.db.model.WorkerModel;
import io.vivarium.net.Constants;
import io.vivarium.net.jobs.SimulationJob;
import io.vivarium.net.messages.CreateJobMessage;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.RequestResourceMessage;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.net.messages.WorkerPledgeMessage;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class Server extends WebSocketServer
{
    private final static InetSocketAddress PORT = new InetSocketAddress(Constants.DEFAULT_PORT);

    private ClientConnectionManager connectionManager = new ClientConnectionManager();

    private ObjectMapper mapper = new ObjectMapper();

    private Connection _databaseConnection;

    public Server() throws UnknownHostException
    {
        super(PORT);
        try
        {
            _databaseConnection = DatabaseUtils.createDatabaseConnection("vivarium", "vivarium", "lifetest");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        System.out.println("SERVER: Web Socket Connection Opened. " + conn + " ~ " + handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        System.out.println(
                "SERVER: Web Socket Connection closed. " + conn + " ~ " + code + " # " + reason + " & " + remote);
    }

    @Override
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
                acceptJob(conn, (CreateJobMessage) untypedMessage);
            }
            else
            {
                System.err.println("SERVER: Unhandled message of type " + untypedMessage.getClass().getSimpleName());
            }
        }
        catch (IOException | SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(
                "SERVER: Web Socket Message . " + conn + " ~ " + message.substring(0, Math.min(message.length(), 200)));
    }

    private synchronized void acceptPledge(WebSocket webSocket, WorkerPledgeMessage pledge) throws SQLException
    {
        WorkerModel worker = new WorkerModel(pledge.workerID, pledge.throughputs, pledge.active, new Date(),
                pledge.fileFormatVersion, pledge.codeVersion);
        worker.persistToDatabase(_databaseConnection);
        connectionManager.registerWorker(pledge.workerID, webSocket);
    }

    private void acceptResource(WebSocket webSocket, SendResourceMessage sendResourceMessage) throws SQLException
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
            jsonString = JSONConverter.serializerToJSONString(collection, sendResourceMessage.resourceID);
        }
        else
        {
            throw new IllegalStateException("Unexpected resource format " + sendResourceMessage.resourceFormat);
        }
        ResourceModel resource = new ResourceModel(sendResourceMessage.resourceID, jsonString,
                Version.FILE_FORMAT_VERSION);
        resource.persistToDatabase(_databaseConnection);
    }

    private void acceptJob(WebSocket conn, CreateJobMessage createJobMessage) throws SQLException
    {
        JobModel job;
        if (createJobMessage.job instanceof SimulationJob)
        {
            SimulationJob simulationJob = (SimulationJob) createJobMessage.job;
            job = new RunSimulationJobModel(simulationJob.jobID, JobStatus.WAITING, (short) 0, null, null, null,
                    simulationJob.endTick, simulationJob.sourceDocumentID, simulationJob.outputDocumentID,
                    simulationJob.dependencies);
        }
        else
        {
            throw new IllegalStateException("Unexpected job type " + createJobMessage.job.getClass().getSimpleName());
        }
        job.persistToDatabase(_databaseConnection);
    }

    private void handleRequestForResource(WebSocket webSocket, RequestResourceMessage requestResourceMessage)
            throws SQLException, IOException
    {
        UUID resourceID = requestResourceMessage.resourceID;
        Optional<ResourceModel> resource = ResourceModel.getFromDatabase(_databaseConnection, resourceID);
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

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        System.out.println("SERVER: Web Socket Error . " + conn + " ~ " + ex);
        ex.printStackTrace();
    }

    public static void main(String[] args)
    {
        System.out.println("SERVER: Running Vivarium Research Server.");
        try
        {
            Server server = new Server();
            server.start();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

}
