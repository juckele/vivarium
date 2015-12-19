package io.vivarium.server;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class VoidFunctionSchedulerTest
{
    @Test
    public void testManualExecute() throws InterruptedException
    {
        Incrementer function = new Incrementer();
        VoidFunctionScheduler scheduler = new VoidFunctionScheduler(function, Long.MAX_VALUE);

        Tester.equal("Initial execute count is zero: ", function.getExecuteCount(), 0);

        scheduler.start();
        Thread.sleep(1);
        Tester.equal("After start, count increments: ", function.getExecuteCount(), 1);

        scheduler.execute();
        Thread.sleep(1);
        Tester.equal("After execution, count increments: ", function.getExecuteCount(), 2);

        scheduler.stop();
    }

    @Test
    public void testAutomaticExecution() throws InterruptedException
    {
        Incrementer function = new Incrementer();
        VoidFunctionScheduler scheduler = new VoidFunctionScheduler(function, 4);

        Tester.equal("Initial execute count is zero: ", function.getExecuteCount(), 0);

        scheduler.start();
        Thread.sleep(6);
        Tester.equal("Wait for 1.5x scheduler period, count increments twice: ", function.getExecuteCount(), 2);

        scheduler.stop();
    }

    private static class Incrementer implements VoidFunction
    {
        int executeCount = 0;

        @Override
        public synchronized void execute()
        {
            executeCount++;
        }

        public int getExecuteCount()
        {
            return executeCount;
        }
    }
}
