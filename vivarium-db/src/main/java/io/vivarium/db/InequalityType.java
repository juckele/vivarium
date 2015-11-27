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
