package com.johnuckele.vivarium.core;

import java.util.ArrayList;

import com.johnuckele.vivarium.audit.AuditFunction;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializedParameter;

@SuppressWarnings("serial")
public class Blueprint implements MapSerializer
{
    // World Generation
    @SerializedParameter
    private int _width = 25;
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
    private ArrayList<AuditFunction> _auditFunctions;

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

    public ArrayList<AuditFunction> getAuditFunctions()
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

    public void setAuditFunctions(ArrayList<AuditFunction> auditFunctions)
    {
        this._auditFunctions = auditFunctions;
    }

    public static Blueprint makeDefault()
    {
        Blueprint wb = new Blueprint();
        wb.finalizeSerialization();
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        wb._auditFunctions = new ArrayList<AuditFunction>();
        return wb;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
