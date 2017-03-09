package io.vivarium.visualizer.enums;

public enum SimulationSpeed
{
    MAXIMUM_THROUGHPUT(1000, 1),
    EXTRA_FAST(100, 1),
    FAST(10, 1),
    REALTIME(1, 1),
    BRISK(1, 6),
    MEDIUM(1, 30),
    SLOW(1, 60);

    private final int _ticks;
    private final int _perFrame;

    SimulationSpeed(int ticks, int perFrame)
    {
        _ticks = ticks;
        _perFrame = perFrame;
    }

    public int getTicks()
    {
        return _ticks;
    }

    public int getPerFrame()
    {
        return _perFrame;
    }

    public static SimulationSpeed getDefault()
    {
        return SLOW;
    }

    public boolean getEnableInterpolation()
    {
        return _perFrame > _ticks;
    }
}