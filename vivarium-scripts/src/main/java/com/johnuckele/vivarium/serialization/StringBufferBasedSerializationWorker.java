package com.johnuckele.vivarium.serialization;

public abstract class StringBufferBasedSerializationWorker extends SerializationWorker
{
    private StringBuffer _buffer;

    public StringBufferBasedSerializationWorker()
    {
        this._buffer = new StringBuffer();
    }

    public String extractStringRepresentation()
    {
        return this._buffer.toString();
    }

    protected StringBuffer getStringBuffer()
    {
        return _buffer;
    }
}
