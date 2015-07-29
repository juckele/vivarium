package com.johnuckele.vivarium.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanParameter
{
    boolean required();

    boolean defaultValue();
}
