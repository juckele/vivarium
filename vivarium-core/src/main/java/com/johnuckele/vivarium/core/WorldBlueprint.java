package com.johnuckele.vivarium.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;
import com.johnuckele.vivarium.serialization.annotations.BooleanParameter;
import com.johnuckele.vivarium.serialization.annotations.ComplexParameter;
import com.johnuckele.vivarium.serialization.annotations.DoubleParameter;
import com.johnuckele.vivarium.serialization.annotations.IntegerParameter;

public class WorldBlueprint implements MapSerializer
{
    // World Generation
    @IntegerParameter(defaultValue = 25)
    private int                _size;
    @DoubleParameter(defaultValue = 0.01)
    private double             _foodGenerationProbability;
    @DoubleParameter(defaultValue = 0.2)
    private double             _initialFoodGenerationProbability;
    @DoubleParameter(defaultValue = 0.1)
    private double             _initialWallGenerationProbability;

    // Simulation Details
    @BooleanParameter(defaultValue = false)
    private boolean            _soundEnabled;

    // Species
    @ComplexParameter
    private ArrayList<Species> _species;

    // Private constructor for deserialization
    private WorldBlueprint()
    {
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

    public int getSize()
    {
        return _size;
    }

    public boolean getSoundEnabled()
    {
        return this._soundEnabled;
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

    public void setSize(int _size)
    {
        this._size = _size;
    }

    public void setSoundEnabled(boolean soundEnabled)
    {
        this._soundEnabled = soundEnabled;
    }

    public void setSpecies(ArrayList<Species> _species)
    {
        this._species = _species;
    }

    public static WorldBlueprint makeUninitialized()
    {
        WorldBlueprint wb = new WorldBlueprint();
        return wb;
    }

    public static WorldBlueprint makeDefault()
    {
        WorldBlueprint wb = new WorldBlueprint();
        SerializationEngine.deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        return wb;
    }

    public static WorldBlueprint makeFromMap(HashMap<String, String> blueprintValues)
    {
        WorldBlueprint wb = new WorldBlueprint();
        SerializationEngine.deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        return wb;
    }

    public static WorldBlueprint makeWithSize(int size)
    {
        WorldBlueprint wb = new WorldBlueprint();
        SerializationEngine.deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb.setSize(size);
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        return wb;
    }

    public static WorldBlueprint makeWithSizeAndSpecies(int size, Species s)
    {
        WorldBlueprint wb = new WorldBlueprint();
        SerializationEngine.deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb.setSize(size);
        wb._species = new ArrayList<Species>();
        wb._species.add(s);
        return wb;
    }

    public static WorldBlueprint makeWithSizeAndSpecies(int size, Collection<Species> s)
    {
        WorldBlueprint wb = new WorldBlueprint();
        SerializationEngine.deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb.setSize(size);
        wb._species = new ArrayList<Species>(s);
        return wb;
    }

    public static WorldBlueprint makeCopy(WorldBlueprint original)
    {
        // TODO Fixup
        throw new UnsupportedOperationException("Here");
    }

    @Override
    public Set<MapSerializer> getReferences()
    {
        return new HashSet<MapSerializer>(_species);
    }

    @Override
    public Map<String, String> finalizeSerialization(Map<String, String> map, Map<MapSerializer, Integer> referenceMap)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void finalizeDeserialization(Map<String, String> map, Map<Integer, MapSerializer> dereferenceMap)
    {
        // TODO Auto-generated method stub

    }

    public static void main(String[] args)
    {
        WorldBlueprint wb = WorldBlueprint.makeDefault();
        SerializationEngine se = new SerializationEngine(null);
        SerializedCollection collection = se.serialize(wb);
        System.out.println(collection);
    }
}
