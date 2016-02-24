package io.vivarium.db;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import io.vivarium.util.Reflection;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class DatabaseUtils
{
    public static Connection createDatabaseConnection(String databaseName, String username, String password)
            throws SQLException
    {
        String url = "jdbc:postgresql://localhost/" + databaseName;
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("stringtype", "unspecified");
        Connection dbConnection = DriverManager.getConnection(url, props);
        dbConnection.setAutoCommit(false);
        return dbConnection;
    }

    public static List<Map<String, Object>> select(Connection connection, String tableName,
            Optional<WhereCondition> condition) throws SQLException
    {
        List<Map<String, Object>> results = new LinkedList<>();

        try (Statement queryStatement = connection.createStatement())
        {
            // Build the select query
            StringBuilder selectStringBuilder = new StringBuilder();
            selectStringBuilder.append("SELECT * FROM ");
            selectStringBuilder.append(tableName);
            if (condition.isPresent())
            {
                selectStringBuilder.append(" WHERE ");
                selectStringBuilder.append(condition.get().toString());
            }

            // Execute the select
            try (ResultSet resultSet = queryStatement.executeQuery(selectStringBuilder.toString()))
            {
                // Build column list
                ResultSetMetaData resultMetaData = resultSet.getMetaData();
                int columnCount = resultMetaData.getColumnCount();
                LinkedList<String> columnNames = new LinkedList<>();
                for (int i = 1; i <= columnCount; i++)
                {
                    columnNames.add(resultMetaData.getColumnName(i));
                }

                // Build relation objects
                while (resultSet.next())
                {
                    Map<String, Object> relation = new HashMap<>();
                    for (String columnName : columnNames)
                    {
                        relation.put(columnName, resultSet.getObject(columnName));
                    }
                    results.add(relation);
                }
            }
        }
        return results;

    }

    public static void upsert(Connection connection, String tableName, Map<String, Object> relation,
            List<String> keyColumns) throws SQLException
    {
        try (Statement sqlStatement = connection.createStatement();)
        {
            // Build lists for all columns and non-key columns for streaming over while we build the the SQL statements.
            List<String> allColumns = new LinkedList<>();
            allColumns.addAll(relation.keySet());
            List<String> nonKeyColumns = new LinkedList<>();
            for (String columnName : relation.keySet())
            {
                if (!keyColumns.contains(columnName))
                {
                    nonKeyColumns.add(columnName);
                }
            }

            // The values to actually use in the SQL. SQL null is fine for null values, and we can call toString() on
            // primitives directly, but String based values need to be wrapped with single quotes for sql.
            Map<String, String> sqlStrings = new HashMap<>();
            for (String columnName : allColumns)
            {
                sqlStrings.put(columnName, toSqlString(relation.get(columnName)));
            }

            // Builds an update string in the form
            // UPDATE table
            // SET key1=value1, key2=value2
            // WHERE id_key = id_value;
            StringBuilder updateStringBuilder = new StringBuilder();
            updateStringBuilder.append("UPDATE ");
            updateStringBuilder.append(tableName);
            updateStringBuilder.append(" SET ");
            updateStringBuilder.append(Joiner.on(", ")
                    .join(nonKeyColumns.stream().map(i -> String.format("%s=%s", i, sqlStrings.get(i))).iterator()));
            updateStringBuilder.append(" WHERE ");
            updateStringBuilder.append(Joiner.on(", ")
                    .join(keyColumns.stream().map(i -> String.format("%s=%s", i, sqlStrings.get(i))).iterator()));
            updateStringBuilder.append(";");

            // Builds an insert string in the form
            // INSERT INTO table (key1, key2, id_key)
            // SELECT value1, value2, id_value
            // WHERE NOT EXISTS (SELECT 1 FROM table WHERE id_key = id_value);
            StringBuilder insertStringBuilder = new StringBuilder();
            insertStringBuilder.append("INSERT INTO ");
            insertStringBuilder.append(tableName);
            insertStringBuilder.append(" (");
            insertStringBuilder.append(Joiner.on(", ").join(allColumns));
            insertStringBuilder.append(") SELECT ");
            insertStringBuilder.append(Joiner.on(", ")
                    .join(allColumns.stream().map(i -> String.format("%s", sqlStrings.get(i))).iterator()));
            insertStringBuilder.append(" WHERE NOT EXISTS (SELECT 1 FROM ");
            insertStringBuilder.append(tableName);
            insertStringBuilder.append(" WHERE ");
            insertStringBuilder.append(Joiner.on(", ")
                    .join(keyColumns.stream().map(i -> String.format("%s=%s", i, sqlStrings.get(i))).iterator()));
            insertStringBuilder.append(");");

            System.out.println(updateStringBuilder.toString().substring(0,
                    Math.min(updateStringBuilder.toString().length(), 500)));
            System.out.println(insertStringBuilder.toString().substring(0,
                    Math.min(insertStringBuilder.toString().length(), 500)));
            // Run the upsert statements.
            sqlStatement.execute(updateStringBuilder.toString());
            sqlStatement.execute(insertStringBuilder.toString());
        }
    }

    static String toSqlString(Object object)
    {
        // Quick exit for nulls
        if (object == null)
        {
            return "null";
        }
        // Quick recur for Optionals
        if (object.getClass() == Optional.class)
        {
            if (((Optional<?>) object).isPresent())
            {
                return toSqlString(((Optional<?>) object).get());
            }
            else
            {
                return toSqlString(null);
            }
        }

        // Type conversion if required
        if (object.getClass() == Version.class)
        {
            // DB stores Version objects as arrays, so convert this to an array for easy encoding.
            object = ((Version) object).toArray();
        }

        // Generate the string
        if (Reflection.isPrimitive(object.getClass()))
        {
            return object.toString();
        }
        else if (object.getClass().isArray())
        {
            StringBuilder arrayString = new StringBuilder();
            arrayString.append("'{");
            List<String> elements = new LinkedList<>();
            for (int i = 0; i < Array.getLength(object); i++)
            {
                elements.add(toSqlString(Array.get(object, i)));
            }
            arrayString.append(Joiner.on(", ").join(elements));
            arrayString.append("}'");
            return arrayString.toString();
        }
        else if (object.getClass() == UUID.class)
        {
            return '\'' + object.toString() + "\'::uuid";
        }
        else
        {
            return '\'' + object.toString() + '\'';
        }
    }

    public static void upsert(Connection connection, String tableName, List<Map<String, Object>> relations,
            List<String> keyColumns) throws SQLException
    {
        for (Map<String, Object> relation : relations)
        {
            upsert(connection, tableName, relation, keyColumns);
        }
    }

    /**
     * Inserts and deletes entries from a junction table until a select against the provided keyColumn returns the
     * exactly the same entries as the provided relations.
     *
     * @param connection
     *            The active connection to the database to update
     * @param tableName
     *            The name of the table. The named table should probably be a junction table.
     * @param relations
     *            A list of relations that should be in the junction table after completion.
     * @param keyColumn
     *            The column name to select against.
     * @param keyValue
     *            The column name to select against.
     * @throws SQLException
     */
    public static void updateJunctionTable(Connection connection, String tableName, List<Map<String, Object>> relations,
            String keyColumn, Object keyValue) throws SQLException
    {
        boolean deleteOnly = false;
        // Build lists for all columns and non-key columns for streaming over while we build the the SQL statements.
        List<String> allColumns = new LinkedList<>();
        List<String> nonKeyColumns = new LinkedList<>();
        if (relations.size() == 0)
        {
            deleteOnly = true;
            allColumns.add(keyColumn);
        }
        else
        {
            Map<String, Object> sampleRelation = relations.get(0); // Assumes that all relations define the same keys.
            allColumns.addAll(sampleRelation.keySet());
            for (String columnName : sampleRelation.keySet())
            {
                if (!keyColumn.equals(columnName))
                {
                    nonKeyColumns.add(columnName);
                }
            }
        }

        try (Statement sqlStatement = connection.createStatement();)
        {
            // Builds an delete string in the form
            // DELETE FROM tableName
            // WHERE keyColumn = keyValue
            // AND column1 NOT IN(relations.get(0).get(column1), relations.get(1).get(column1));
            StringBuilder deleteStringBuilder = new StringBuilder();
            deleteStringBuilder.append("DELETE FROM ");
            deleteStringBuilder.append(tableName);
            deleteStringBuilder.append(" WHERE ");
            deleteStringBuilder.append(keyColumn);
            deleteStringBuilder.append('=');
            deleteStringBuilder.append(toSqlString(keyValue));
            for (String nonKeyColumn : nonKeyColumns)
            {
                deleteStringBuilder.append(" AND ");
                deleteStringBuilder.append(nonKeyColumn);
                deleteStringBuilder.append(" NOT IN(");
                deleteStringBuilder.append(
                        Joiner.on(", ").join(relations.stream().map(i -> toSqlString(i.get(nonKeyColumn))).iterator()));
                deleteStringBuilder.append(")");
            }

            StringBuilder insertStringBuilder = new StringBuilder();
            insertStringBuilder.append("INSERT INTO ");
            insertStringBuilder.append(tableName);
            insertStringBuilder.append(" (");
            insertStringBuilder.append(Joiner.on(", ").join(allColumns));
            insertStringBuilder.append(") ");
            boolean firstSelect = true;
            for (Map<String, Object> relation : relations)
            {
                if (!firstSelect)
                {
                    insertStringBuilder.append(" UNION ");
                }
                else
                {
                    firstSelect = false;
                }
                insertStringBuilder.append("SELECT ");
                insertStringBuilder.append(Joiner.on(", ").join(
                        allColumns.stream().map(i -> String.format("%s", toSqlString(relation.get(i)))).iterator()));
                insertStringBuilder.append(" WHERE NOT EXISTS (SELECT 1 FROM ");
                insertStringBuilder.append(tableName);
                insertStringBuilder.append(" WHERE ");
                insertStringBuilder.append(Joiner.on(" AND ").join(allColumns.stream()
                        .map(i -> String.format("%s=%s", i, toSqlString(relation.get(i)))).iterator()));
                insertStringBuilder.append(')');
            }
            insertStringBuilder.append(';');

            System.out.println(deleteStringBuilder.toString().substring(0,
                    Math.min(deleteStringBuilder.toString().length(), 500)));
            // Run the delete & insert statements.
            sqlStatement.execute(deleteStringBuilder.toString());
            if (!deleteOnly)
            {
                System.out.println(insertStringBuilder.toString().substring(0,
                        Math.min(insertStringBuilder.toString().length(), 500)));
                sqlStatement.execute(insertStringBuilder.toString());
            }
        }
    }

    public static Object toPrimitiveArray(java.sql.Array array, Class<?> clazz) throws SQLException
    {
        if (clazz == long.class)
        {
            Long[] objectArray = (Long[]) array.getArray();
            List<Long> list = Arrays.asList(objectArray);
            return Longs.toArray(list);
        }
        else if (clazz == int.class)
        {
            Integer[] objectArray = (Integer[]) array.getArray();
            List<Integer> list = Arrays.asList(objectArray);
            return Ints.toArray(list);
        }
        else
        {
            throw new IllegalArgumentException("Unable to convert to " + clazz + " array");
        }
    }
}
