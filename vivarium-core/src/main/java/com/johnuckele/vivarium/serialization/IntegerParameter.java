package com.johnuckele.vivarium.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntegerParameter
{
    boolean required();

    int defaultValue();
}
