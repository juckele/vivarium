package com.johnuckele.vivarium.serialization;

public abstract class FileBasedSerializationWorker extends SerializationWorker
{
    private String _fileName;

    public FileBasedSerializationWorker(String fileName)
    {
        _fileName = fileName;
    }
}
