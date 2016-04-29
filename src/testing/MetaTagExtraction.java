package testing;

import java.sql.*;
import java.util.HashMap;

/**
 * Attempting to extract the relevant metatags from the images.db
 * file.
 *
 * Created by swebo_000 on 2016-04-27.
 */
public class MetaTagExtraction {

    private static HashMap<String, Integer> tagCountMap = new HashMap<String, Integer>();
    private static Connection c;

    private static void connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:images.db");
    }

    private static void getAllTags() throws SQLException {
        String query = "SELECT Metadata FROM Images";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {

        }
    }

    public static void main(String[] args) {
        try {
            connect();
            getAllTags();
        } catch (Exception e) {
            System.err.println(e.getClass() + " " + e.getMessage());
            System.exit(-1);
        }

    }
}
