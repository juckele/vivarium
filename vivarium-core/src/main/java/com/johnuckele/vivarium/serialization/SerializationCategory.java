package com.johnuckele.vivarium.serialization;

public enum SerializationCategory
{
    WORLD(6), AUDIT(5), CREATURE(4), BRAIN(3), BLUEPRINT(2), SPECIES(1);

    private int _rank;

    SerializationCategory(int rank)
    {
        _rank = rank;
    }

    public int getRank()
    {
        return _rank;
    }

    public static SerializationCategory[] rankedValues()
    {
        SerializationCategory[] result = { SPECIES, BLUEPRINT, BRAIN, CREATURE, AUDIT, WORLD };
        return result;
    }
}
