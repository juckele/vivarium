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
public class Blueprint extends VivariumObject
{
    // World Generation
    @SerializedParameter
    private int _width = 25;
    @SerializedParameter
    private int _height = 25;
    @SerializedParameter
    private double _foodGenerationProbability = 0.01;
    @SerializedParameter
    private double _initialFoodGenerationProbability = 0.0;
    @SerializedParameter
    private double _initialWallGenerationProbability = 0.1;

    // Simulation Details
    @SerializedParameter
    private boolean _soundEnabled = false;

    // Species
    @SerializedParameter
    private ArrayList<Species> _species;

    // Audit Functions
    @SerializedParameter
    private ArrayList<AuditBlueprint> _auditFunctions;

    // Private constructor for deserialization
    private Blueprint()
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

    public double getInitialWallGenerationProbability()
    {
        return _initialWallGenerationProbability;
    }

    public boolean getSoundEnabled()
    {
        return this._soundEnabled;
    }

    public ArrayList<AuditBlueprint> getAuditFunctions()
    {
        return this._auditFunctions;
    }

    public ArrayList<Species> getSpecies()
    {
        return this._species;
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

    public void setSpecies(ArrayList<Species> species)
    {
        this._species = species;
    }

    public void setAuditFunctions(ArrayList<AuditBlueprint> auditFunctions)
    {
        this._auditFunctions = auditFunctions;
    }

    public static Blueprint makeDefault()
    {
        Blueprint wb = new Blueprint();
        wb.finalizeSerialization();
        wb._species = new ArrayList<>();
        wb._species.add(Species.makeDefault());
        wb._auditFunctions = new ArrayList<>();
        return wb;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }

    public static void main(String[] args)
    {
        System.out.println("" + Blueprint.makeDefault().hashCode() + ":" + Blueprint.makeDefault().toString());
    }
}
