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

    @Override
    public abstract String toString();
}
