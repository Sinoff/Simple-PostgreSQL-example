package maze.data;

import javafx.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by dvird on 17/03/29.
 */
public class DBConnector {


    public static Connection getConnection()
    {
        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("config.properties");
        Properties props = new Properties();
        try {
            props.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Connection connection = null;


        try {

            connection = DriverManager.getConnection(props.getProperty("database"), props);

        } catch (SQLException e) {

            System.out.println("Connection Failed!");
            e.printStackTrace();
            return null;

        }

        if (connection != null) {

        } else {
            System.out.println("Failed to make connection!");
        }
        return connection;
    }


    public static ArrayList<Pair<String, String>> getSchema(ResultSet results)
    {

        ArrayList<Pair<String, String>> typedSchema = new ArrayList<>();
//        ArrayList<String> schema = new ArrayList<>();
        try {
            final ResultSetMetaData metaData = results.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                schema.add(metaData.getColumnLabel(i));
                typedSchema.add(new Pair(
                    metaData.getColumnLabel(i), metaData.getColumnTypeName(i)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typedSchema;
    }

    public static void printSchema(ResultSet results)
    {
        ArrayList<Pair<String, String>> schema = getSchema(results);
        printSchema(schema);
    }

    public static void printSchema(ArrayList<Pair<String, String>> schema )
    {
        StringBuilder sb = new StringBuilder();
        sb.append("|| ");
        for (Pair field : schema)
        {
            sb.append(field).append(" ||");
        }
        System.out.println(sb.toString());
    }

    public static void printResults(ResultSet results)
    {
        ArrayList<Pair<String, String>> schema = getSchema(results);
        printSchema(schema);
        StringBuilder sb = new StringBuilder();

        try {
            while(results.next())
            {
                sb.append("|| ");
                for (Pair<String, String> field : schema)
                {
                    sb.append(results.getString(field.getKey())).append(" || ");
                }
                sb.append(System.lineSeparator());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(sb.toString());

    }

    public static void printTablesSchemas()
    {
        Connection connection = getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT table_name\n" +
                    "  FROM information_schema.tables\n" +
                    " WHERE table_schema='public'\n" +
                    "   AND table_type='BASE TABLE';");
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next())
            {
                String tableName = resultSet.getString("table_name");
                System.out.println(tableName);
                pstmt = connection.prepareStatement("SELECT * From "+ tableName);
                ResultSet tableResultSet = pstmt.executeQuery();
                printSchema(tableResultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
