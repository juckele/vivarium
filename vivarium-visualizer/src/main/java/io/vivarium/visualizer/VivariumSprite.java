package io.vivarium.visualizer;

public enum VivariumSprite
{
    FLOOR(0, 0),
    WALL(1, 0),
    FOOD(2, 0),
    FOOD_GENERATOR_INACTIVE(3, 0),
    FOOD_GENERATOR_ACTIVE(4, 0),
    HALO_CREATURE_1(0, 1),
    HALO_CREATURE_2(1, 1),
    HALO_CREATURE_3(2, 1),
    FLAMETHROWER_INACTIVE(0, 2),
    FLAMETHROWER_ACTIVE(1, 2),
    FLAME_1(2, 2),
    FLAME_2(3, 2),
    FLAME_3(4, 2),
    CREATURE_1(0, 4),
    CREATURE_2(1, 4),
    CREATURE_3(2, 4);

    public final int x;
    public final int y;

    VivariumSprite(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}
