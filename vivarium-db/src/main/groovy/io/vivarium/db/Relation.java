/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

public class Relation<T> implements WhereCondition
{
    public final RelationType type;
    public final String columnName;
    public final T value;

    private Relation(RelationType type, String columnName, T value)
    {
        this.type = type;
        this.columnName = columnName;
        this.value = value;
    }

    public static <T> Relation<T> equalTo(String columnName, T value)
    {
        return new Relation<T>(RelationType.EQUALS, columnName, value);
    }

    @Override
    public String toString()
    {
        return columnName + type.toString() + DatabaseUtils.toSqlString(value);
    }
}
