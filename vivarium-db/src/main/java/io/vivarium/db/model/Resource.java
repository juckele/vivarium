package io.vivarium.db.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class Resource
{
    public final UUID resourceID;
    public final Optional<String> jsonData;
    public final Optional<Integer> fileFormatVersion;

    private Resource(UUID resourceID, String jsonData, Integer fileFormatVersion)
    {
        this.resourceID = resourceID;
        this.jsonData = Optional.of(jsonData);
        this.fileFormatVersion = Optional.of(fileFormatVersion);
    }

    public static Optional<Resource> getFromDatabase(Connection connection, UUID resourceID) throws SQLException
    {
        Statement queryStatement = connection.createStatement();
        // queryStatement.setObject(1, resourceID.toString());
        // System.out.println(queryStatement);
        System.out.println("Hello");
        String queryString = "SELECT * FROM resources WHERE id = '" + resourceID.toString() + "'";
        System.out.println(queryString);
        ResultSet resourceResultSet = queryStatement.executeQuery(queryString);
        System.out.println("Queired");
        while (resourceResultSet.next())
        {
            String jsonData = resourceResultSet.getString("data");
            Integer version = (Integer) resourceResultSet.getObject("file_format_version");
            return Optional.of((new Resource(resourceID, jsonData, version)));
        }
        return Optional.empty();
    }

    public static Resource create(UUID resourceID, String jsonData)
    {
        return new Resource(resourceID, jsonData, Version.FILE_FORMAT_VERSION);
    }

    public void persistToDatabase(Connection connection) throws SQLException
    {
        Statement insertStatement = connection.createStatement();
        String insertString = String.format("INSERT INTO resources VALUES ('%s', '%s', %s)", resourceID.toString(),
                jsonData.get(), fileFormatVersion.get());
        insertStatement.execute(insertString);
    }
}
