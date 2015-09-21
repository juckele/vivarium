package com.johnuckele.vivarium.serialization;

public class ArchaicSerializedParameter
{
    private String                _parameterName;
    private Class<?>              _clazz;
    private Class<?>              _genericClazz;
    private SerializationCategory _referenceCategory;
    private boolean               _hasDefaultValue;
    private String                _defaultValue;

    public ArchaicSerializedParameter(String parameterName, Class<?> clazz, Object defaultValue)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _hasDefaultValue = true;
        _defaultValue = "" + defaultValue;
    }

    public ArchaicSerializedParameter(String parameterName, Class<?> clazz, SerializationCategory referenceCategory,
            Object defaultValue)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _referenceCategory = referenceCategory;
        _hasDefaultValue = true;
        _defaultValue = "" + defaultValue;
    }

    public ArchaicSerializedParameter(String parameterName, Class<?> clazz)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _hasDefaultValue = false;
    }

    public ArchaicSerializedParameter(String parameterName, Class<?> clazz, SerializationCategory referenceCategory)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _referenceCategory = referenceCategory;
        _hasDefaultValue = false;
    }

    public ArchaicSerializedParameter(String parameterName, Class<?> clazz, Class<?> genericClazz)
    {
        _parameterName = parameterName;
        _clazz = clazz;
        _genericClazz = clazz;
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

    public boolean hasGenericClass()
    {
        return _genericClazz != null;
    }

    public Class<?> getGenericClass()
    {
        if (_genericClazz == null)
        {
            throw new IllegalStateException("Parameter " + this._parameterName + " has no _genericClazz");
        }
        return _genericClazz;
    }

    public boolean hasReferenceCategory()
    {
        return _referenceCategory != null;
    }

    public SerializationCategory getReferenceCategory()
    {
        if (_referenceCategory == null)
        {
            throw new IllegalStateException("Parameter " + this._parameterName + " has no _referenceCategory");
        }
        return _referenceCategory;
    }

    public String getDefaultValue()
    {
        if (!_hasDefaultValue)
        {
            throw new IllegalStateException("Parameter " + this._parameterName + " has no _defaultValue");
        }
        return _defaultValue;
    }

    public String getFieldName()
    {
        return "_" + _parameterName;
    }

}
