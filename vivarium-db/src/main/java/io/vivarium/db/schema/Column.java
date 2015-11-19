package io.vivarium.db.schema;

public class Column
{
    public final String name;
    public final String type;
    public final String references;
    public final Boolean primaryKey;
    public final Boolean notNull;
    public final Boolean unique;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private Column()
    {
        name = null;
        type = null;
        references = null;
        primaryKey = null;
        notNull = null;
        unique = null;
    }

    public Column(boolean testCreate)
    {
        name = "id";
        type = "uuid";
        references = null;
        primaryKey = null;
        notNull = null;
        unique = null;
    }

    @Override
    public String toString()
    {
        return (primaryKey != null && primaryKey ? "key " : "") + name;
    }
}
