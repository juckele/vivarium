/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

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
        Thread.sleep(2);
        Tester.equal("After start, count increments: ", function.getExecuteCount(), 1);

        scheduler.execute();
        Thread.sleep(2);
        Tester.equal("After execution, count increments: ", function.getExecuteCount(), 2);

        scheduler.stop();
    }

    @Test
    public void testManualExecuteQueue() throws InterruptedException
    {
        SlowIncrementer function = new SlowIncrementer(4);
        VoidFunctionScheduler scheduler = new VoidFunctionScheduler(function, Long.MAX_VALUE);

        Tester.equal("Initial execute count is zero: ", function.getExecuteCount(), 0);

        scheduler.start();
        Thread.sleep(10);
        Tester.equal("After start, count increments: ", function.getExecuteCount(), 1);

        scheduler.execute();
        scheduler.execute();
        Tester.equal("Neither execute has been counted yet: ", function.getExecuteCount(), 1);
        Thread.sleep(10);
        Tester.equal("But both will be completed as soon as possible: ", function.getExecuteCount(), 3);

        scheduler.execute();
        scheduler.execute();
        scheduler.execute();
        Thread.sleep(10);
        Tester.equal("The queue only stores a single extra execute though, additionals are lost: ",
                function.getExecuteCount(), 5);

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
        int _executeCount = 0;

        @Override
        public void execute()
        {
            _executeCount++;
        }

        public int getExecuteCount()
        {
            return _executeCount;
        }
    }

    private static class SlowIncrementer implements VoidFunction
    {
        int _executeCount = 0;
        final long _delay;

        public SlowIncrementer(long delay)
        {
            this._delay = delay;
        }

        @Override
        public void execute()
        {
            try
            {
                Thread.sleep(_delay);
                _executeCount++;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public int getExecuteCount()
        {
            return _executeCount;
        }
    }
}
