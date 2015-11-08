/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.visualization.animation;

import io.vivarium.core.Direction;
import io.vivarium.core.World;

public abstract class GraphicalDelegate
{
    protected World _renderWorld;
    int _selectedCreatureID;

    public abstract void drawImage(int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
            int colorOffset, Direction heading);

    public abstract void requestRender();

    public final void renderFrame()
    {
        startFrameRender();
        if (_renderWorld != null)
        {
            WorldRenderer.renderWorld(this, _renderWorld, null, 0, _selectedCreatureID);
        }
        endFrameRender();
    }

    protected abstract void startFrameRender();

    protected abstract void endFrameRender();

    public final void render(World world, int selectedCreatureID)
    {
        _renderWorld = world;
        _selectedCreatureID = selectedCreatureID;
        requestRender();
    }
}
