/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.common.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Message
{
    protected Message()
    {
    }
}
