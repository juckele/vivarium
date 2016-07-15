package io.vivarium.net;

import java.io.File;
import java.util.concurrent.ExecutionException;

import io.vivarium.client.TaskClient;
import io.vivarium.client.WorkerClient;
import io.vivarium.client.WorkerConfig;
import io.vivarium.client.task.DownloadResourceTask;
import io.vivarium.client.task.UploadResourceTask;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.server.VivariumResearchServer;
import io.vivarium.util.UUID;

public class DemoTest
{
    public static void main(String[] args) throws Exception
    {
        // Run Server
        VivariumResearchServer.main(args);

        Thread.sleep(100);

        World world = new World(WorldBlueprint.makeDefault());
        UploadResourceTask t1 = new UploadResourceTask(UUID.randomUUID(), world);
        TaskClient c1 = new TaskClient(t1);
        c1.connect();
        System.out.println("New world with " + world.getCreatureCount() + " creatures");

        /*
         * List<UUID> dependencies = new LinkedList<>();
         * dependencies.add(UUID.fromString("b51738f3-9f08-42ce-969d-ffae6c722e6e"));
         * dependencies.add(UUID.fromString("7bd92d3d-f0f0-4760-9ad3-2f4302530e2c")); Job job = new
         * SimulationJob(dependencies, UUID.fromString("d51b6b31-84b5-0835-d5d5-05467ab4f04d"),
         * UUID.fromString("d51b6b31-84b5-0835-d5d5-05467ab4f04d"), 20000); TaskClient c2 = new TaskClient(new
         * CreateJobTask(job)); c2.connect();
         */

        Thread.sleep(100);

        WorkerClient w = new WorkerClient(WorkerConfig.loadWorkerConfig(new File(WorkerConfig.DEFAULT_PATH), true));
        w.connect();

        Thread.sleep(100);

        // Don't try to request before they have it.
        Thread.sleep(300);
        DownloadResourceTask t3 = new DownloadResourceTask(t1.getResourceUUID());
        TaskClient c3 = new TaskClient(t3);
        c3.connect();
        try
        {
            World world_copy = t3.waitForResource().getFirst(World.class);
            System.out.println("DL world with " + world_copy.getCreatureCount() + " creatures");
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        // Thread.sleep(1000);
        System.exit(0);
    }
}
