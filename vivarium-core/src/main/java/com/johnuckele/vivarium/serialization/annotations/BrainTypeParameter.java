package com.johnuckele.vivarium.serialization.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.johnuckele.vivarium.core.brain.BrainType;

@Retention(RetentionPolicy.RUNTIME)
public @interface BrainTypeParameter
{
    BrainType defaultValue();
}
