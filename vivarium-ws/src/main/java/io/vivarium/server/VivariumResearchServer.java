/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.net.InetSocketAddress;
import java.sql.Connection;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.net.Constants;
import io.vivarium.persistence.PersistenceModule;

public class VivariumResearchServer implements StartableStoppable
{
    private final ServerNetworkModule _networkModule;
    private boolean _running = false;

    public VivariumResearchServer(ServerNetworkModule networkModule)
    {
        _networkModule = networkModule;
    }

    @Override
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

    @Override
    public void stop()
    {
        // TODO: IMPLEMENT
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("SERVER: Initializing Vivarium Research Server.");

        // Build server dependencies
        Connection databaseConnection = DatabaseUtils.createDatabaseConnection("vivarium", "vivarium", "lifetest");
        PersistenceModule persistenceModule = new PersistenceModule(databaseConnection);
        ClientConnectionManager clientConnectionManager = new ClientConnectionManager();
        WorkloadEnforcer workloadEnforcer = new WorkloadEnforcer(persistenceModule, clientConnectionManager);
        VoidFunctionScheduler enforcerScheduler = new VoidFunctionScheduler(workloadEnforcer,
                WorkloadEnforcer.DEFAULT_ENFORCE_TIME_GAP_IN_MS);
        MessageRouter messageRouter = new MessageRouter(persistenceModule, clientConnectionManager, enforcerScheduler);
        InetSocketAddress port = new InetSocketAddress(Constants.DEFAULT_PORT);
        ServerNetworkModule networkModule = new ServerNetworkModule(port, messageRouter);

        // Build & start server
        VivariumResearchServer server = new VivariumResearchServer(networkModule);
        server.start();
    }

}
