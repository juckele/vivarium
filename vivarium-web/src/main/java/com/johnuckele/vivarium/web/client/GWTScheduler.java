package com.johnuckele.vivarium.web.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.johnuckele.vivarium.visualization.animation.SchedulingDelegate;

public class GWTScheduler extends SchedulingDelegate
{
    private VivariumWeb _webApp;
    private int tick;
    private double lastTickTimestamp;

    public GWTScheduler(VivariumWeb webApp)
    {
        _webApp = webApp;
    }

    @Override
    public void startFirstTime()
    {
        AnimationScheduler.get().requestAnimationFrame(_webApp);
    }

    public void execute(double timestamp)
    {
        if (tick > 0)
        {
            if (timestamp >= lastTickTimestamp + 1000)
            {
                lastTickTimestamp = timestamp;
                _visualizer.tickWorld();
            }
        }
        else
        {
            if (lastTickTimestamp == 0)
            {
                lastTickTimestamp = timestamp;
            }
            if (timestamp >= lastTickTimestamp + 1000)
            {
                lastTickTimestamp = timestamp;
                _visualizer.tickWorld();
            }
        }
        _visualizer.renderWorld();
    }
}
