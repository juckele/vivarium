/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

public class Inequality<T> implements WhereCondition
{
    public final InequalityType type;
    public final String columnName;
    public final T value;

    private Inequality(InequalityType type, String columnName, T value)
    {
        this.type = type;
        this.columnName = columnName;
        this.value = value;
    }

    public static <T> Inequality<T> equalTo(String columnName, T value)
    {
        return new Inequality<T>(InequalityType.EQUALS, columnName, value);
    }

    @Override
    public String toString()
    {
        return columnName + type.toString() + DatabaseUtils.toSqlString(value);
    }
}
