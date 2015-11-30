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
import io.vivarium.util.UUID;

public class DemoTest
{
    public static void main(String[] args) throws UnknownHostException, URISyntaxException, InterruptedException
    {
        Server s = new Server();
        s.start();

        Thread.sleep(100);

        TaskClient c = new TaskClient(new CreateAndUploadWorldTask());
        c.connect();

        Thread.sleep(100);

        WorkerClient w = new WorkerClient(UUID.fromString("c02f97b1-5cba-8f27-22a9-29895e37bb3f"));
        w.connect();

        Thread.sleep(100);

        // Don't try to request before they have it.
        Thread.sleep(300);
        TaskClient c2 = new TaskClient(new DownloadWorldTask());
        c2.connect();

        // Thread.sleep(1000);
        // System.exit(0);
    }
}
