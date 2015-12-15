/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.sql.Connection;

public class WorkloadManager
{
    private final Connection _databaseConnection;
    private final ClientConnectionManager _clientConnectionManager;

    public WorkloadManager(Connection databaseConnection, ClientConnectionManager clientConnectionManager)
    {
        _databaseConnection = databaseConnection;
        _clientConnectionManager = clientConnectionManager;
    }

    private class WorkloadManagerHelper extends Thread
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub

        }

    }
}
