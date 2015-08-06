package com.johnuckele.vivarium.serialization;

public class SerializedParameter
{
    private String                _parameterName;
    private Class<?>              _clazz;
    private SerializationCategory _references;
    private String                _defaultValue;

    public SerializedParameter(String parameterType, Class<?> clazz, Object defaultValue)
    {
        _parameterName = parameterType;
        _clazz = clazz;
        _defaultValue = "" + defaultValue;
    }

    public SerializedParameter(String parameterType, Class<?> clazz, SerializationCategory references,
            Object defaultValue)
    {
        _parameterName = parameterType;
        _clazz = clazz;
        _references = references;
        _defaultValue = "" + defaultValue;
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
        return _references;
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
