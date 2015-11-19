package io.vivarium.db.schema;

import java.util.LinkedList;

import com.google.common.base.Joiner;

public class Schema
{
    public final LinkedList<Table> tables;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private Schema()
    {
        tables = null;
    }

    public Schema(boolean testCreate)
    {
        tables = new LinkedList<Table>();
        tables.add(new Table(testCreate));
    }

    @Override
    public String toString()
    {
        return Joiner.on("\n").join(tables);
    }
}
