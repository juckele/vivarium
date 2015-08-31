package com.johnuckele.vivarium.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.audit.AuditRecordType;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class Blueprint implements MapSerializer
{
    // World Generation
    private int    _size;
    private double _foodGenerationProbability;
    private double _initialFoodGenerationProbability;
    private double _initialWallGenerationProbability;

    // Simulation Details
    private boolean                    _soundEnabled;
    private ArrayList<AuditRecordType> _auditRecordTypes;

    // Species
    private ArrayList<Species> _species;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("size", Integer.class, 25));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("foodGenerationProbability", Double.class, 0.01));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("initialFoodGenerationProbability", Double.class, 0.2));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("initialWallGenerationProbability", Double.class, 0.1));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("soundEnabled", Boolean.class, false));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("auditRecordTypes", ArrayList.class, "[]"));
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("species", ArrayList.class, SerializationCategory.SPECIES, "[]"));
    }

    // Private constructor for deserialization
    private Blueprint()
    {
    }

    public void addAuditRecord(AuditRecordType auditRecord)
    {
        this._auditRecordTypes.add(auditRecord);
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

    public ArrayList<AuditRecordType> getAuditRecordTypes()
    {
        return this._auditRecordTypes;
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
        wb._auditRecordTypes = new ArrayList<AuditRecordType>();
        return wb;
    }

    public static Blueprint makeFromMap(HashMap<String, Object> blueprintValues)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, blueprintValues);
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        wb._auditRecordTypes = new ArrayList<AuditRecordType>();
        return wb;
    }

    public static Blueprint makeWithSize(int size)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb.setSize(size);
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        wb._auditRecordTypes = new ArrayList<AuditRecordType>();
        return wb;
    }

    public static Blueprint makeWithSizeAndSpecies(int size, Species s)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb.setSize(size);
        wb._species = new ArrayList<Species>();
        wb._species.add(s);
        wb._auditRecordTypes = new ArrayList<AuditRecordType>();
        return wb;
    }

    public static Blueprint makeWithSizeAndSpecies(int size, Collection<Species> s)
    {
        Blueprint wb = new Blueprint();
        new SerializationEngine().deserialize(wb, SerializationEngine.EMPTY_OBJECT_MAP);
        wb.setSize(size);
        wb._species = new ArrayList<Species>(s);
        wb._auditRecordTypes = new ArrayList<AuditRecordType>();
        return wb;
    }

    @Override
    public List<MapSerializer> getReferences()
    {
        return new LinkedList<MapSerializer>(_species);
    }

    @Override
    public SerializationCategory getSerializationCategory()
    {
        return SerializationCategory.BLUEPRINT;
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
    public List<SerializedParameter> getMappedParameters()
    {
        return Blueprint.SERIALIZED_PARAMETERS;
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "size":
                return this._size;
            case "foodGenerationProbability":
                return this._foodGenerationProbability;
            case "initialFoodGenerationProbability":
                return "" + this._initialFoodGenerationProbability;
            case "initialWallGenerationProbability":
                return "" + this._initialWallGenerationProbability;
            case "soundEnabled":
                return "" + this._soundEnabled;
            case "auditRecordTypes":
                return this._auditRecordTypes;
            case "species":
                return this._species;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "size":
                this._size = (Integer) value;
                break;
            case "foodGenerationProbability":
                this._foodGenerationProbability = (Double) value;
                break;
            case "initialFoodGenerationProbability":
                this._initialFoodGenerationProbability = (Double) value;
                break;
            case "initialWallGenerationProbability":
                this._initialWallGenerationProbability = (Double) value;
                break;
            case "soundEnabled":
                this._soundEnabled = (Boolean) value;
                break;
            case "auditRecordTypes":
                this._auditRecordTypes = (ArrayList<AuditRecordType>) value;
                break;
            case "species":
                this._species = (ArrayList<Species>) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }
}
