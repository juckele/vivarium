/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

import io.vivarium.client.Worker;
import io.vivarium.server.Server;

public class DemoTest
{
    public static void main(String[] args) throws UnknownHostException, URISyntaxException
    {
        Server s = new Server();
        s.start();

        Worker w = new Worker();
        w.connect();
    }
}
