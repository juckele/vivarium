package io.vivarium.client;

import com.google.gwt.core.shared.GWT;

import io.vivarium.serialization.VivariumObject;
import io.vivarium.serialization.VivariumObjectCopier;

public class StreamingObjectCopier implements VivariumObjectCopier
{
    private final VivariumObjectMapper _mapper = GWT.create(VivariumObjectMapper.class);

    @SuppressWarnings("unchecked")
    @Override
    public <T extends VivariumObject> T copyObject(T object)
    {
        return (T) _mapper.read(_mapper.write(object));
    }

}
