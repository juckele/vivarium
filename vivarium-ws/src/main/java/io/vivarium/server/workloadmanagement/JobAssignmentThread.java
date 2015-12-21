package io.vivarium.server.workloadmanagement;

public class JobAssignmentThread
{
    private final JobAssignmentOperation _jobAssingmentOperation;
    private final ThreadHelper _helper = new ThreadHelper();

    public JobAssignmentThread(JobAssignmentOperation jobAssingmentOperation)
    {
        _jobAssingmentOperation = jobAssingmentOperation;
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

    }
}
