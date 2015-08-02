package com.johnuckele.vivarium.serialization;

public class SerializedParameter
{
    private String _parameterName;
    private String _defaultValue;

    /*
     * public SerializedParameter(String parameterType, Class<?> clazz, double defaultValue) { if (clazz !=
     * Double.class) { throw new IllegalArgumentException(); } _parameterName = parameterType; _clazz = clazz;
     * _defaultValue = "" + defaultValue; }
     */

    public SerializedParameter(String parameterType, Object defaultValue)
    {
        _parameterName = parameterType;
        _defaultValue = "" + defaultValue;
    }

    public String getName()
    {
        return _parameterName;
    }

    public String getDefaultValue()
    {
        return _defaultValue;
    }
}
