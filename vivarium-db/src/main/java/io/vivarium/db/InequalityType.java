/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

public enum InequalityType
{
    EQUALS
    {
        @Override
        public String toString()
        {
            return "=";
        }
    };
}
