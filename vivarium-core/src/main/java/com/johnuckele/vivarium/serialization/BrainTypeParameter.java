package com.johnuckele.vivarium.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.johnuckele.vivarium.core.brain.BrainType;

@Retention(RetentionPolicy.RUNTIME)
public @interface BrainTypeParameter
{
    boolean required();

    BrainType defaultValue();
}
