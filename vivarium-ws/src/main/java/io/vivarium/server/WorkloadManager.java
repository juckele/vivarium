/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import io.vivarium.persistence.PersistenceModule;

public class WorkloadManager
{
    private final PersistenceModule _persistenceModule;
    private final ClientConnectionManager _clientConnectionManager;

    public WorkloadManager(PersistenceModule persistenceModule, ClientConnectionManager clientConnectionManager)
    {
        _persistenceModule = persistenceModule;
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
