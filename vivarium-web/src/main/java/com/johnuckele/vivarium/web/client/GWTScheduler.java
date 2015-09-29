package com.johnuckele.vivarium.web.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.johnuckele.vivarium.visualization.animation.SchedulingDelegate;

public class GWTScheduler extends SchedulingDelegate
{
    private long startTime;

    @Override
    public void startFirstTime()
    {
        startTime = System.currentTimeMillis();
        Scheduler.get().scheduleEntry(new GWTThread());
    }

    private class GWTThread implements RepeatingCommand
    {
        @Override
        public boolean execute()
        {
            long currentTime = System.currentTimeMillis() - startTime;
            if (currentTime > 1000)
            {
                _visualizer.tickWorld();
                startTime += 1000;
            }
            _visualizer.renderWorld();
            return true;
        }
    }

    public void execute(double timestamp)
    {
        _visualizer.renderWorld();
    }
}
