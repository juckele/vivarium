package com.johnuckele.vivarium.serialization;

import com.johnuckele.vivarium.core.Species;

public class JSONSerializationWorker extends StringBufferBasedSerializationWorker
{

    public JSONSerializationWorker()
    {
    }

    @Override
    public void serialize(Species s)
    {
        StringBuffer buffer = this.getStringBuffer();
    }
}
