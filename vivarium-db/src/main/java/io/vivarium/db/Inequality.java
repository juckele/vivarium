package io.vivarium.db;

public class Inequality<T>
{
    public final InequalityType type;
    public final T value;

    private Inequality(InequalityType type, T value)
    {
        this.type = type;
        this.value = value;
    }

    public static <T> Inequality<T> equalTo(T value)
    {
        return new Inequality<T>(InequalityType.EQUALS, value);
    }
}
