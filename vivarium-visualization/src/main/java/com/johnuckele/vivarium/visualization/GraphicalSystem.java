package com.johnuckele.vivarium.visualization;

import com.johnuckele.vivarium.core.Direction;

public abstract class GraphicalSystem
{
    public abstract void drawImage(int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
            Direction heading);
}
