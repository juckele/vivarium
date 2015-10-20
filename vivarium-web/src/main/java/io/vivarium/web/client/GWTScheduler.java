/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web.client;

import com.google.gwt.animation.client.AnimationScheduler;

import io.vivarium.visualization.animation.SchedulingDelegate;

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
        // In GWT, on the first frame we want to request an Animation Frame as that's where we trigger our actual visual
        // renders from and also where we call the scheduler again.
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
