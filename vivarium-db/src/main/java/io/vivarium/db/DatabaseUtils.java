/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;

import io.vivarium.util.Reflection;

public class DatabaseUtils
{
    public static Connection createDatabaseConnection(String databaseName, String username, String password)
            throws SQLException
    {
        String url = "jdbc:postgresql://localhost/" + databaseName;
        Connection dbConnection = DriverManager.getConnection(url, username, password);
        return dbConnection;
    }

    public static void upsert(Connection connection, String tableName, Map<String, Object> relation,
            List<String> keyColumns) throws SQLException
    {
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        innerUpsert(connection, tableName, relation, keyColumns);
        connection.commit();
        connection.setAutoCommit(originalAutoCommit);
    }

    public static void upsert(Connection connection, String tableName, List<Map<String, Object>> relations,
            List<String> keyColumns) throws SQLException
    {
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        for (Map<String, Object> relation : relations)
        {
            innerUpsert(connection, tableName, relation, keyColumns);
        }
        connection.commit();
        connection.setAutoCommit(originalAutoCommit);
    }

    private static void innerUpsert(Connection connection, String tableName, Map<String, Object> relation,
            List<String> keyColumns) throws SQLException
    {
        // Build lists for all columns and non-key columns for streaming over while we build the the SQL statements.
        List<String> allColumns = new LinkedList<String>();
        allColumns.addAll(relation.keySet());
        List<String> nonKeyColumns = new LinkedList<String>();
        for (String columnName : relation.keySet())
        {
            if (!keyColumns.contains(columnName))
            {
                nonKeyColumns.add(columnName);
            }
        }

        // The values to actually use in the SQL. SQL null is fine for null values, and we can call toString() on
        // primitives directly, but String based values need to be wrapped with single quotes for sql.
        Map<String, String> sqlStrings = new HashMap<String, String>();
        for (String columnName : allColumns)
        {
            Object columnValue = relation.get(columnName);
            if (columnValue == null)
            {
                sqlStrings.put(columnName, "null");
            }
            else if (Reflection.isPrimitive(columnValue.getClass()))
            {
                sqlStrings.put(columnName, relation.get(columnName).toString());
            }
            else
            {
                sqlStrings.put(columnName, '\'' + relation.get(columnName).toString() + '\'');
            }
        }

        // Builds an update string in the form
        // UPDATE table
        // SET key1=value1, key2=value2
        // WHERE id_key = id_value;
        StringBuilder updateStringBuilder = new StringBuilder();
        updateStringBuilder.append("UPDATE ");
        updateStringBuilder.append(tableName);
        updateStringBuilder.append(" SET ");
        updateStringBuilder
                .append(Joiner.on(", ").join(nonKeyColumns.stream().map(i -> String.format("%s=?", i)).iterator()));
        updateStringBuilder.append(" WHERE ");
        updateStringBuilder
                .append(Joiner.on(", ").join(keyColumns.stream().map(i -> String.format("%s=?", i)).iterator()));
        updateStringBuilder.append(";");

        // Builds an insert string in the form
        // INSERT INTO table (key1, key2)
        // SELECT value1, value2
        // WHERE NOT EXISTS (SELECT 1 FROM table WHERE id_key = id_value);
        StringBuilder insertStringBuilder = new StringBuilder();
        insertStringBuilder.append("INSERT INTO ");
        insertStringBuilder.append(tableName);
        insertStringBuilder.append(" (");
        insertStringBuilder.append(Joiner.on(", ").join(allColumns));
        insertStringBuilder.append(") SELECT ");
        insertStringBuilder.append(Joiner.on(", ").join(nonKeyColumns.stream().map(i -> "?").iterator()));
        insertStringBuilder.append(" WHERE NOT EXISTS (SELECT 1 FROM ");
        insertStringBuilder.append(tableName);
        insertStringBuilder.append(" WHERE ");
        insertStringBuilder.append(Joiner.on(", ")
                .join(keyColumns.stream().map(i -> String.format("%s=?", i, sqlStrings.get(i))).iterator()));
        insertStringBuilder.append(");");

        try (PreparedStatement updateStatement = connection.prepareStatement(updateStringBuilder.toString());
                PreparedStatement insertStatement = connection.prepareStatement(insertStringBuilder.toString());)
        {
            // Bind all of the variables
            int parameterIndex = 1;
            for (String columnName : nonKeyColumns)
            {
                updateStatement.setObject(parameterIndex, sqlStrings.get(columnName));
                insertStatement.setObject(parameterIndex, sqlStrings.get(columnName));
                parameterIndex++;
            }
            for (String columnName : keyColumns)
            {
                updateStatement.setObject(parameterIndex, sqlStrings.get(columnName));
                insertStatement.setObject(parameterIndex, sqlStrings.get(columnName));
                parameterIndex++;
            }

            // Run the upsert statements.
            updateStatement.executeUpdate();
            insertStatement.executeUpdate();
        }
    }
}
