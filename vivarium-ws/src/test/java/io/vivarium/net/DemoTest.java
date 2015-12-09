/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

import io.vivarium.client.TaskClient;
import io.vivarium.client.WorkerClient;
import io.vivarium.client.task.CreateAndUploadWorldTask;
import io.vivarium.client.task.DownloadWorldTask;
import io.vivarium.server.Server;

public class DemoTest
{
    public static void main(String[] args) throws UnknownHostException, URISyntaxException, InterruptedException
    {
        Server s = new Server();
        s.start();

        Thread.sleep(100);

        TaskClient c1 = new TaskClient(new CreateAndUploadWorldTask());
        c1.connect();

        /*
         * List<UUID> dependencies = new LinkedList<>();
         * dependencies.add(UUID.fromString("b51738f3-9f08-42ce-969d-ffae6c722e6e"));
         * dependencies.add(UUID.fromString("7bd92d3d-f0f0-4760-9ad3-2f4302530e2c")); Job job = new
         * SimulationJob(dependencies, UUID.fromString("d51b6b31-84b5-0835-d5d5-05467ab4f04d"),
         * UUID.fromString("d51b6b31-84b5-0835-d5d5-05467ab4f04d"), 20000); TaskClient c2 = new TaskClient(new
         * CreateJobTask(job)); c2.connect();
         */

        Thread.sleep(100);

        WorkerClient w = new WorkerClient();
        w.connect();

        Thread.sleep(100);

        // Don't try to request before they have it.
        Thread.sleep(300);
        TaskClient c3 = new TaskClient(new DownloadWorldTask());
        c3.connect();

        Thread.sleep(1000);
        System.exit(0);
    }
}
