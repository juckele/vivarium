package io.vivarium.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

import io.vivarium.db.schema.Column;
import io.vivarium.db.schema.Schema;
import io.vivarium.db.schema.Table;

public class DatabaseUtils
{
    private static final String SCHEMA_NAME = "public";

    public static Connection createDatabaseConnection(String databaseName) throws SQLException
    {
        String url = "jdbc:postgresql://localhost/" + databaseName;
        String username = "vivarium";
        String password = "lifetest";
        Connection dbConnection = DriverManager.getConnection(url, username, password);
        return dbConnection;
    }

    public static void applySchema(Connection connection, Schema loadedSchema, boolean dropMissingElements)
            throws SQLException
    {
        HashSet<String> existingTables = findExistingTables(connection);
        HashSet<String> desiredTables = new HashSet<String>();
        // Statement statement = connection.createStatement();
        // Create or update tables as required
        for (Table table : loadedSchema.tables)
        {
            desiredTables.add(table.name);
            if (existingTables.contains(table.name))
            {
                updateTable(connection, table, dropMissingElements);
            }
            else
            {
                createTable(connection, table);
            }
        }
        // iff we are dropping elements, drop tables that are not present in the Schema object.
        if (dropMissingElements)
        {
            for (String existingTable : existingTables)
            {
                if (!desiredTables.contains(existingTable))
                {
                    dropTable(connection, existingTable);
                }
            }
        }
    }

    private static void createTable(Connection connection, Table table) throws SQLException
    {
        Statement statement = connection.createStatement();
        StringBuilder sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append("CREATE TABLE").append(' ');
        sqlStringBuilder.append(table.name).append(" (");
        sqlStringBuilder.append(describeColumns(table)).append(')');
        String sqlString = sqlStringBuilder.toString();
        System.out.println("Attempting to create table: " + sqlString);
        statement.executeUpdate(sqlString);
    }

    private static String describeColumns(Table table)
    {
        System.out.println(table);
        List<String> columnStrings = new LinkedList<String>();
        for (Column column : table.columns)
        {
            System.out.println(column);
            columnStrings.add(describeColumn(column));
        }
        System.out.println(columnStrings);
        return Joiner.on(", ").join(columnStrings);
    }

    private static String describeColumn(Column column)
    {
        StringBuilder columnStringBuilder = new StringBuilder();
        columnStringBuilder.append(column.name);
        columnStringBuilder.append(' ');
        columnStringBuilder.append(column.type);
        if (column.primaryKey != null && column.primaryKey)
        {
            columnStringBuilder.append(' ');
            columnStringBuilder.append("PRIMARY KEY");
        }
        if (column.notNull != null && column.notNull)
        {
            columnStringBuilder.append(' ');
            columnStringBuilder.append("NOT NULL");
        }
        if (column.unique != null && column.unique)
        {
            columnStringBuilder.append(' ');
            columnStringBuilder.append("UNIQUE");
        }
        if (column.references != null)
        {
            columnStringBuilder.append(' ');
            columnStringBuilder.append("REFERENCES");
            columnStringBuilder.append(' ');
            columnStringBuilder.append(column.references);
        }
        System.out.println("Describing col... " + columnStringBuilder.toString());
        return columnStringBuilder.toString();
    }

    private static void updateTable(Connection connection, Table table, boolean dropMissingElements) throws SQLException
    {
        // TODO: Finish this method. For now this method does not work.
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet columnResults = metadata.getColumns(null, SCHEMA_NAME, table.name, null);
        ResultSetMetaData rsmd = columnResults.getMetaData();
        while (columnResults.next())

        {
            System.out.println("");
            for (int i = 1; i <= rsmd.getColumnCount(); i++)
            {
                System.out.println(rsmd.getColumnLabel(i));
                System.out.println(columnResults.getString(i));
            }

            // String columnName = columnResults.getString("COLUMN_NAME");
            // String columnType = columnResults.getString("TYPE_NAME");
            // String columnSize = columnResults.getString("COLUMN_SIZE");
            // boolean notNull = !columnResults.getBoolean("NULLABLE");
        }
    }

    private static void dropTable(Connection connection, String tableName) throws SQLException
    {
        Statement statement = connection.createStatement();
        String sqlString = "DROP TABLE " + tableName;
        System.out.println("Attempting to drop table: " + sqlString);
        statement.executeQuery(sqlString);
    }

    private static HashSet<String> findExistingTables(Connection connection) throws SQLException
    {
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet tableResults = metadata.getTables(null, SCHEMA_NAME, "%", null);
        HashSet<String> existingTables = new HashSet<String>();
        while (tableResults.next())
        {
            if (tableResults.getString("TABLE_TYPE").equals("TABLE"))
            {
                System.out.println("Found existing table: " + tableResults.getString("TABLE_NAME"));
                existingTables.add(tableResults.getString("TABLE_NAME"));
            }
        }
        return existingTables;
    }
}
