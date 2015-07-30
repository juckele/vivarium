package com.johnuckele.vivarium.serialization.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleParameter
{
    double defaultValue();
}
