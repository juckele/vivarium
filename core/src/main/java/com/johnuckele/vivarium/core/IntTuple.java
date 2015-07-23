package com.johnuckele.vivarium.core;

import java.io.Serializable;

public class IntTuple implements Serializable
{
    private static final long serialVersionUID = -1L;

    public int                a;
    public int                b;

    public IntTuple(int a, int b)
    {
        this.a = a;
        this.b = b;
    }
}
