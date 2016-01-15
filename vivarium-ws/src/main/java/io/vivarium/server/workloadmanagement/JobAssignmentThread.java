package io.vivarium.server.workloadmanagement;

import io.vivarium.server.ClientConnectionManager;

public class JobAssignmentThread
{
    // private final ClientConnectionManager _clientConnectionManager;
    // private final JobAssignmentOperation _jobAssingmentOperation;
    private final ThreadHelper _helper = new ThreadHelper();

    public JobAssignmentThread(ClientConnectionManager clientConnectionManager,
            JobAssignmentOperation jobAssingmentOperation)
    {
        // _clientConnectionManager = clientConnectionManager;
        // _jobAssingmentOperation = jobAssingmentOperation;
    }

    public void start()
    {
        _helper.start();
    }

    public void join()
    {
        try
        {
            _helper.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private class ThreadHelper extends Thread
    {
        @Override
        public void run()
        {
            // ClientConnection connection = _clientConnectionManager
            // .getConnectionForWorker(_jobAssingmentOperation.getWorkerID());
            // TODO Auto-generated method stub
        }

    }
}
