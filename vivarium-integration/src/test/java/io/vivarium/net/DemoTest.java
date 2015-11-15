/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

import io.vivarium.client.Client;
import io.vivarium.client.CreateAndUploadWorldTask;
import io.vivarium.client.DownloadWorldTask;
import io.vivarium.client.Worker;
import io.vivarium.server.Server;

public class DemoTest
{
    public static void main(String[] args) throws UnknownHostException, URISyntaxException, InterruptedException
    {
        Server s = new Server();
        s.start();

        Client c = new Client(new CreateAndUploadWorldTask());
        c.connect();

        Worker w = new Worker();
        w.connect();

        // Don't try to request before they have it.
        Thread.sleep(1000);
        Client c2 = new Client(new DownloadWorldTask());
        c2.connect();

        // Thread.sleep(1000);
        // System.exit(0);
    }
}
