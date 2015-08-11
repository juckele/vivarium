package com.johnuckele.vivarium.serialization;

public class SerializedParameter
{
    private String                _parameterName;
    private Class<?>              _clazz;
    private SerializationCategory _referenceCategory;
    private boolean               _hasDefaultValue;
    private String                _defaultValue;

    public SerializedParameter(String parameterName, Class<?> clazz, Object defaultValue)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _hasDefaultValue = true;
        _defaultValue = "" + defaultValue;
    }

    public SerializedParameter(String parameterName, Class<?> clazz, SerializationCategory referenceCategory,
            Object defaultValue)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _referenceCategory = referenceCategory;
        _hasDefaultValue = true;
        _defaultValue = "" + defaultValue;
    }

    public SerializedParameter(String parameterName, Class<?> clazz)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _hasDefaultValue = false;
    }

    public SerializedParameter(String parameterName, Class<?> clazz, SerializationCategory referenceCategory)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _referenceCategory = referenceCategory;
        _hasDefaultValue = false;
    }

    public String getName()
    {
        return _parameterName;
    }

    public Class<?> getValueClass()
    {
        return _clazz;
    }

    public SerializationCategory getReferenceCategory()
    {
        return _referenceCategory;
    }

    public boolean hasDefaultValue()
    {
        return _hasDefaultValue;
    }

    public String getDefaultValue()
    {
        return _defaultValue;
    }

    public String getFieldName()
    {
        return "_" + _parameterName;
    }

}
