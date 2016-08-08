package io.vivarium.core;

import java.util.ArrayList;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class WorldBlueprint extends VivariumObject
{
    // World Generation
    @SerializedParameter
    private int _width = 25;
    @SerializedParameter
    private int _height = 25;
    @SerializedParameter
    private double _foodGenerationProbability = 0.005;
    @SerializedParameter
    private double _initialFoodGenerationProbability = 0.0;
    @SerializedParameter
    private double _initialWallGenerationProbability = 0.1;
    @SerializedParameter
    private double _foodGeneratorProbability = 0.005;
    @SerializedParameter
    private double _flamethrowerProbability = 0.01;

    // Simulation Details
    @SerializedParameter
    private boolean _soundEnabled = false;
    // Simulation Details
    @SerializedParameter
    private boolean _signEnabled = false;

    // Blueprints for creatures
    @SerializedParameter
    private ArrayList<CreatureBlueprint> _creatureBlueprints;

    // Audit Functions
    @SerializedParameter
    private ArrayList<AuditBlueprint> _auditBlueprints;

    // Private constructor for deserialization
    private WorldBlueprint()
    {
    }

    public void setSize(int size)
    {
        this._width = size;
        this._height = size;
    }

    public void setHeight(int height)
    {
        this._height = height;
    }

    public void setWidth(int width)
    {
        this._width = width;
    }

    public int getHeight()
    {
        return this._height;
    }

    public int getWidth()
    {
        return this._width;
    }

    public double getFoodGenerationProbability()
    {
        return _foodGenerationProbability;
    }

    public double getInitialFoodGenerationProbability()
    {
        return _initialFoodGenerationProbability;
    }

    public double getFoodGeneratorProbability()
    {
        return _foodGeneratorProbability;
    }

    public double getFlamethrowerProbability()
    {
        return _flamethrowerProbability;
    }

    public double getInitialWallGenerationProbability()
    {
        return _initialWallGenerationProbability;
    }

    public boolean getSoundEnabled()
    {
        return this._soundEnabled;
    }

    public boolean getSignEnabled()
    {
        return this._signEnabled;
    }

    public ArrayList<AuditBlueprint> getAuditBlueprints()
    {
        return this._auditBlueprints;
    }

    public ArrayList<CreatureBlueprint> getCreatureBlueprints()
    {
        return this._creatureBlueprints;
    }

    public void setFoodGenerationProbability(double p)
    {
        this._foodGenerationProbability = p;
    }

    public void setInitialFoodGenerationProbability(double p)
    {
        this._initialFoodGenerationProbability = p;
    }

    public void setInitialFoodGenerationProbability(int p)
    {
        this._initialFoodGenerationProbability = p;

    }

    public void setInitialWallGenerationProbability(double p)
    {
        this._initialWallGenerationProbability = p;
    }

    public void setSoundEnabled(boolean soundEnabled)
    {
        this._soundEnabled = soundEnabled;
    }

    public void setSignEnabled(boolean signEnabled)
    {
        this._signEnabled = signEnabled;
    }

    public void setCreatureBlueprints(ArrayList<CreatureBlueprint> creatureBlueprints)
    {
        this._creatureBlueprints = creatureBlueprints;
    }

    public void setAuditBlueprints(ArrayList<AuditBlueprint> auditBlueprints)
    {
        this._auditBlueprints = auditBlueprints;
    }

    public static WorldBlueprint makeDefault()
    {
        WorldBlueprint wb = new WorldBlueprint();
        wb.finalizeSerialization();
        wb._creatureBlueprints = new ArrayList<>();
        wb._creatureBlueprints.add(CreatureBlueprint.makeDefault());
        wb._auditBlueprints = new ArrayList<>();
        return wb;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }

    public static void main(String[] args)
    {
        System.out
                .println("" + WorldBlueprint.makeDefault().hashCode() + ":" + WorldBlueprint.makeDefault().toString());
    }
}
