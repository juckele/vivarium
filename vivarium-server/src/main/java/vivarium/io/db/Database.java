package vivarium.io.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;

public class Database
{
    public static void main(String[] args) throws SQLException
    {
        System.out.println("Testing the database");
        String url = "jdbc:postgresql://localhost/vivarium";
        String username = "vivarium";
        String password = "lifetest";
        Connection db = DriverManager.getConnection(url, username, password);
        Statement statement = db.createStatement();
        String queryString = "SELECT * FROM weather";
        ResultSet results = statement.executeQuery(queryString);
        System.out.println(results);
        ResultSetMetaData metaData = results.getMetaData();
        String[] cols = new String[metaData.getColumnCount()];
        for (int i = 0; i < metaData.getColumnCount(); i++)
        {
            cols[i] = metaData.getColumnLabel(i + 1);
        }
        LinkedList<HashMap<String, String>> rows = new LinkedList<HashMap<String, String>>();
        while (results.next())
        {
            HashMap<String, String> row = new HashMap<String, String>();
            for (int i = 0; i < cols.length; i++)
            {
                row.put(cols[i], results.getString(cols[i]));
                rows.add(row);
            }
        }
        System.out.println(rows);
        db.close();
        System.out.println("Everything seemed okay!");
    }
}
