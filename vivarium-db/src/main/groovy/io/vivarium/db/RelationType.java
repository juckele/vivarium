package io.vivarium.db;

public enum RelationType
{
    EQUALS
    {
        @Override
        public String toString()
        {
            return "=";
        }
    }
}
