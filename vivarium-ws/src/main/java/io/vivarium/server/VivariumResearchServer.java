/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.net.InetSocketAddress;
import java.sql.Connection;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.net.Constants;

public class VivariumResearchServer
{
    private final ServerNetworkModule _networkModule;
    private boolean _running = false;

    public VivariumResearchServer(ServerNetworkModule networkModule)
    {
        _networkModule = networkModule;
    }

    public void start()
    {
        if (!_running)
        {
            System.out.println("SERVER: Starting Network");
            _networkModule.start();
            System.out.println("SERVER: Network Ready");
        }
        else
        {
            throw new IllegalStateException("Server already running");
        }
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("SERVER: Initializing Vivarium Research Server.");

        // Build server dependencies
        Connection databaseConnection = DatabaseUtils.createDatabaseConnection("vivarium", "vivarium", "lifetest");
        ClientConnectionManager clientConnectionManager = new ClientConnectionManager();
        WorkloadManager workloadManager = new WorkloadManager(databaseConnection, clientConnectionManager);
        MessageRouter messageRouter = new MessageRouter(databaseConnection, clientConnectionManager, workloadManager);
        InetSocketAddress port = new InetSocketAddress(Constants.DEFAULT_PORT);
        ServerNetworkModule networkModule = new ServerNetworkModule(port, messageRouter);

        // Build & start server
        VivariumResearchServer server = new VivariumResearchServer(networkModule);
        server.start();
    }

}
