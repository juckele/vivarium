/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web;

import com.github.nmorel.gwtjackson.client.AbstractConfiguration;

import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class Configuration extends AbstractConfiguration
{
    @Override
    protected void configure()
    {
        type(UUID.class).serializer(ToStringSerializer.class).deserializer(UUIDDeserializer.class);
        type(Version.class).serializer(ToStringSerializer.class).deserializer(VersionDeserializer.class);
    }

}
