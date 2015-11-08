/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web.client;

import com.google.gwt.animation.client.AnimationScheduler;

import io.vivarium.visualization.animation.SchedulingDelegate;

public class GWTScheduler extends SchedulingDelegate
{
    private VivariumWeb _webApp;
    private double lastTickTimestamp;
    private boolean _tickEveryFrame;
    private int _ticksPerStep;

    public GWTScheduler(VivariumWeb webApp, boolean tickEveryFrame, int ticksPerStep)
    {
        _webApp = webApp;
        _tickEveryFrame = tickEveryFrame;
        _ticksPerStep = ticksPerStep;
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
        if (lastTickTimestamp == 0)
        {
            lastTickTimestamp = timestamp;
        }
        if (timestamp >= lastTickTimestamp + 1000 || _tickEveryFrame)
        {
            lastTickTimestamp = timestamp;
            for (int i = 0; i < _ticksPerStep; i++)
            {
                _visualizer.tickWorld();
            }
        }
        _visualizer.renderWorld();
    }
}
