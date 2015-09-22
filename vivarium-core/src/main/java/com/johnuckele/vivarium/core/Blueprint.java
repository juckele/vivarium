package com.johnuckele.vivarium.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import com.johnuckele.vivarium.audit.AuditFunction;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;
import com.johnuckele.vivarium.serialization.SerializedParameter;

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

    public static Blueprint makeUninitialized()
    {
        Blueprint wb = new Blueprint();
        return wb;
    }

    public static Blueprint makeCopy(Blueprint original)
    {
        return (Blueprint) new SerializationEngine().makeCopy(original);
    }

    public static Blueprint makeDefault()
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        wb._auditFunctions = new ArrayList<AuditFunction>();
        return wb;
    }

    public static Blueprint makeFromMap(Map<String, Object> blueprintValues)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, blueprintValues);
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        wb._auditFunctions = new ArrayList<AuditFunction>();
        return wb;
    }

    public static Blueprint makeWithSize(int size)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb._width = size;
        wb._height = size;
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        wb._auditFunctions = new ArrayList<AuditFunction>();
        return wb;
    }

    public static Blueprint makeWithSizeAndSpecies(int size, Species s)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb._width = size;
        wb._height = size;
        wb._species = new ArrayList<Species>();
        wb._species.add(s);
        wb._auditFunctions = new ArrayList<AuditFunction>();
        return wb;
    }

    public static Blueprint makeWithSizeAndSpecies(int size, Collection<Species> s)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb._width = size;
        wb._height = size;
        wb._species = new ArrayList<Species>(s);
        wb._auditFunctions = new ArrayList<AuditFunction>();
        return wb;
    }

    public static void main(String[] args)
    {
        LinkedList<Species> species = new LinkedList<Species>();
        Species s1 = Species.makeDefault();
        s1.setBaseFoodRate(0);
        species.add(s1);
        Species s2 = Species.makeDefault();
        s2.setMutationRateExponent(-10);
        species.add(s2);
        Blueprint wb = Blueprint.makeWithSizeAndSpecies(25, species);
        SerializationEngine se = new SerializationEngine();
        SerializedCollection collection = se.serialize(wb);
        System.out.println(collection);
    }

    @Override
    public void finalizeSerialization()
    {
        // TODO Auto-generated method stub

    }
}
