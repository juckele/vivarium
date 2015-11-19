package io.vivarium.db.schema;

import java.util.LinkedList;

import com.google.common.base.Joiner;

public class Table
{
    public final LinkedList<Column> columns;
    public final LinkedList<String> constraints;
    public final String name;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private Table()
    {
        columns = null;
        constraints = null;
        name = null;
    }

    public Table(boolean testCreate)
    {
        columns = new LinkedList<Column>();
        columns.add(new Column(testCreate));
        constraints = null;
        name = "Job";
    }

    @Override
    public String toString()
    {
        return "(" + name + ": " + Joiner.on(", ").join(columns) + ")";
    }
}
