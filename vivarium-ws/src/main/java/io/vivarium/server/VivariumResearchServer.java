package io.vivarium.server;

import java.net.InetSocketAddress;
import java.sql.Connection;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.net.Constants;
import io.vivarium.persistence.PersistenceModule;
import io.vivarium.server.workloadmanagement.JobAssignmentThreadFactory;
import io.vivarium.server.workloadmanagement.WorkloadEnforcer;
import io.vivarium.util.concurrency.StartableStoppable;
import io.vivarium.util.concurrency.VoidFunctionScheduler;

public class VivariumResearchServer implements StartableStoppable
{
    private final ServerNetworkModule _networkModule;

    public VivariumResearchServer(ServerNetworkModule networkModule)
    {
        _networkModule = networkModule;
    }

    @Override
    public synchronized void start()
    {
        _networkModule.start();
    }

    @Override
    public synchronized void stop()
    {
        try
        {
            _networkModule.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("SERVER: Initializing Vivarium Research Server.");

        // Build server dependencies
        Connection databaseConnection = DatabaseUtils.createDatabaseConnection("test", "test", "test");
        PersistenceModule persistenceModule = new PersistenceModule(databaseConnection);
        ClientConnectionFactory clientConnectionFactory = new ClientConnectionFactory();
        ClientConnectionManager clientConnectionManager = new ClientConnectionManager(clientConnectionFactory);
        JobAssignmentThreadFactory jobAssignmentThreadFactory = new JobAssignmentThreadFactory(clientConnectionManager);
        WorkloadEnforcer workloadEnforcer = new WorkloadEnforcer(persistenceModule, jobAssignmentThreadFactory);
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
