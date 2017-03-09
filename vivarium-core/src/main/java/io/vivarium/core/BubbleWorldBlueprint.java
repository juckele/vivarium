package io.vivarium.core;

import java.util.ArrayList;

import io.vivarium.core.sensor.Sensor;
import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class BubbleWorldBlueprint extends WorldBlueprint
{
    static
    {
        ClassRegistry.getInstance().register(BubbleWorldBlueprint.class);
    }

    @SerializedParameter
    private double _width;
    @SerializedParameter
    private double _height;

    @SerializedParameter
    private int _initialCreaturePopulation = 10;
    @SerializedParameter
    private int _initialWallCount = 2;

    // Private constructor for deserialization
    private BubbleWorldBlueprint()
    {
    }

    public int getInitialCreaturePopulation()
    {
        return this._initialCreaturePopulation;
    }

    public void setInitialCreaturePopulation(int n)
    {
        this._initialCreaturePopulation = n;
    }

    public void setSize(double size)
    {
        _width = size;
        _height = size;
    }

    @Override
    public void setCreatureBlueprints(ArrayList<CreatureBlueprint> creatureBlueprints)
    {
        this._creatureBlueprints = creatureBlueprints;
    }

    public static BubbleWorldBlueprint makeDefault()
    {
        BubbleWorldBlueprint wb = new BubbleWorldBlueprint();
        wb.finalizeSerialization();
        wb._creatureBlueprints = new ArrayList<>();
        Sensor[] nilSensors = {};
        wb._creatureBlueprints.add(CreatureBlueprint.makeWithSensors(nilSensors, 0, 0, 0));
        wb._auditBlueprints = new ArrayList<>();
        return wb;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
