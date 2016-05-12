package testing;

import Models.*;

import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class DatabaseHandler {

    private static final double ALPHA = 0.3;
    private Connection c;

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            // testing the DatabaseHandler class
            DatabaseHandler d = new DatabaseHandler();
            UserEntity testUser = new UserEntity("forDBtesting2", "mailadress@kth.se", "pass123", "user");
            d.insertUserBucketWeights(testUser);
            //d.insertUser(testUser);
            //ProductEntity p = d.findProduct(testUser, "top", 5);
            System.out.println("Find product: " + (System.currentTimeMillis() - start));
            //d.update(testUser, p, 1);
            System.out.println("Update: " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatabaseHandler() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:outfitterdb.db");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(UserEntity user) throws SQLException{
        String query = "INSERT INTO users VALUES(?,?,?,?,?)";
        PreparedStatement insUser = c.prepareStatement(query);
        insUser.setInt(1, user.getUserId());
        insUser.setString(2, user.getUsername());
        insUser.setString(3, user.getEmail());
        insUser.setString(4, user.getPassword());
        insUser.setString(5, user.getRole());
        insUser.executeUpdate();
        insUser.close();
    }

    public void insertUserTags(UserEntity user) throws SQLException {
        String query = "SELECT tag_id FROM tags";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {
            //query = "INSERT INTO user_weights VALUES(NULL,?,?,?)"; //accommodate for the id field.
            query = "INSERT INTO user_weights VALUES(?,?,?)";
            PreparedStatement insTag = c.prepareStatement(query);
            insTag.setInt(1, user.getUserId());
            insTag.setInt(2, rs.getInt("tag_id"));
            insTag.setDouble(3, Math.random());
            insTag.executeUpdate();
        }
        rs.close();
    }

    public void insertUserBucketWeights(UserEntity user) throws SQLException {
        String topBuckets = "SELECT top.bucket, bottom.bucket FROM " +
                "(SELECT DISTINCT bucket FROM Bucket " +
                "WHERE pid IN (SELECT pid FROM Product WHERE top = 'top')) AS top JOIN " +
                "(SELECT DISTINCT bucket FROM Bucket " +
                "WHERE pid IN (SELECT pid FROM Product WHERE top = 'bottom')) AS bottom";
        ResultSet rs = c.createStatement().executeQuery(topBuckets);
        while(rs.next()) {
            String query = "INSERT INTO Bucket_weight VALUES(?,?,?,?)";
            PreparedStatement ins = c.prepareStatement(query);
            ins.setInt(1, user.getUserId());
            ins.setInt(2, rs.getInt(1));
            ins.setInt(3, rs.getInt(2));
            ins.setDouble(4, Math.random());
            ins.executeUpdate();
        }
        rs.close();
    }

    public ProductEntity findProduct(UserEntity user, String top, int limit) throws SQLException{
       String query = "SELECT pid FROM productTag " +
                "LEFT OUTER JOIN (SELECT weight, tag_id " +
                "FROM user_weights WHERE user_id = ?) AS w " +
                "ON w.tag_id = tag " +
                "WHERE pid IN (SELECT pid FROM Product WHERE top = ?) " +
                "GROUP BY pid ORDER BY SUM(w.weight)/COUNT(*) DESC LIMIT " + limit;
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setInt(1,user.getUserId());
        stmt.setString(2, top);
        ResultSet rs = stmt.executeQuery();
        int row = 1 + (int)(Math.random() * (limit-1));
        int counter = 0;
        while (rs.next() && counter != row) {
            counter++;
        }
        String pid = rs.getString("pid");
        rs.close();
        return new ProductEntity(pid);
    }

    public HashSet<String> getMeta(ProductEntity p) throws  SQLException{
        HashSet<String> tags = new HashSet<String>();
        String query = "SELECT t.name FROM productTag pt, tags t " +
                "WHERE t.tag_id = pt.tag AND pt.pid = '" + p.getPid()+"'";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {
            tags.add(rs.getString("name"));
        }
        return tags;
    }

    public ProductEntity findMatch(ProductEntity p, UserEntity u, int limit) throws SQLException {
        String query = "SELECT pid FROM Bucket " +
                "JOIN (SELECT toBucket, weight FROM Bucket_weight " +
                "WHERE topBucket IN (SELECT bucket FROM Bucket " +
                "WHERE pid = ?) " +
                "AND user = ?) AS w " +
                "ON w.toBucket = bucket ORDER BY w.weight LIMIT " + limit;
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setString(1, p.getPid());
        stmt.setInt(2, u.getUserId());
        ResultSet rs = stmt.executeQuery();
        int row = 1 + (int)(Math.random() * (limit-1));
        int counter = 0;
        while (rs.next() && counter != row) {
            counter++;
        }
        String pid = rs.getString("pid");
        rs.close();
        return new ProductEntity(pid);
    }

    public void update(UserEntity user, ProductEntity product, int like) throws SQLException {
        String weights = "SELECT weight, tag_id FROM user_weights WHERE user_id = ? " +
                " AND tag_id IN (SELECT tag FROM productTag WHERE pid = ?)";
        PreparedStatement weightStmt = c.prepareStatement(weights);
        weightStmt.setInt(1, user.getUserId());
        weightStmt.setString(2, product.getPid());
        ResultSet rs = weightStmt.executeQuery();
        while (rs.next()) {
            String update = "UPDATE user_weights " +
                    "SET weight = ? WHERE user_id = ? AND tag_id = ?";
            PreparedStatement updateStmt = c.prepareStatement(update);
            updateStmt.setDouble(1, rs.getDouble("weight") + (like *ALPHA * Math.abs(like - rs.getDouble("weight"))));
            updateStmt.setInt(2, user.getUserId());
            updateStmt.setInt(3, rs.getInt("tag_id"));
            updateStmt.executeUpdate();
        }
        rs.close();
    }

    public void getTagsFile() throws Exception {
        PrintWriter writer = new PrintWriter("allUsedTags", "UTF-8");
        String query = "SELECT name FROM tags";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {
            writer.println(rs.getString("name"));
        }
        rs.close();
        writer.close();
    }

    public void close() throws SQLException{
        c.close();
    }
}
