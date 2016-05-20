package testing;

import Models.*;

import java.sql.*;
import java.util.*;

public class DatabaseHandler {

    private static final double ALPHA = 0.1;
    private Connection c;

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            // testing the DatabaseHandler class
            DatabaseHandler d = new DatabaseHandler();
            UserEntity testUser = new UserEntity("forDBtesting2", "mailadress@kth.se", "pass123", "salt", "user");
            testUser.setUserId(46);
            System.out.println(d.getCalibrateCount(testUser));
            //d.insertUserBucketWeights(testUser);
            //ProductEntity p = d.findProduct(testUser, "top", 5);
            //System.out.println(p.getPid() + " found: " + (System.currentTimeMillis() - start));

            //ProductEntity match = d.findMatch(new ProductEntity("0264984008"),testUser, 5);
            //System.out.println(match.getPid() + " matching : " + (System.currentTimeMillis() -start));

            //d.updateMatch(testUser,p,match,1);
            //d.update(testUser, p, 1);
            //System.out.println("Update: " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatabaseHandler() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:/Users/swebo_000/IdeaProjects/Outfitter/outfitterdb.db");
            //Class.forName("com.mysql.jdbc.Driver");
            //c = DriverManager.getConnection("jdbc:mysql://c151.lithium.hosting:3306/fypniqhc_outfitter", "fypniqhc_outfitter", "Outfitter12345");
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
        c.setAutoCommit(false);
        String query = "SELECT tag_id FROM tags";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {
            //query = "INSERT INTO user_weights VALUES(NULL,?,?,?)"; //accommodate for the id field.
            query = "INSERT INTO user_weights VALUES(?,?,?)";
            PreparedStatement insTag = c.prepareStatement(query);
            insTag.setInt(1, user.getUserId());
            insTag.setInt(2, rs.getInt("tag_id"));
            insTag.setDouble(3, 0.0);
            insTag.executeUpdate();
        }
        c.commit();
        c.setAutoCommit(true);
        rs.close();
    }

    public void insertUserSettings(UserEntity user) throws SQLException {
        String query = "INSERT INTO user_settings VALUES(?,?,?)";
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setInt(1, user.getUserId());
        stmt.setInt(2, 0);
        stmt.setString(3, "none");
        stmt.executeUpdate();
    }

    public int getCalibrateCount(UserEntity user) throws SQLException {
        String query = "SELECT calibrate FROM user_settings WHERE user_id = ?";
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setInt(1, user.getUserId());
        ResultSet rs = stmt.executeQuery();
        int count = rs.getInt(1);
        stmt.close();
        return count;
    }

    public void incrementCalibrateCount(UserEntity user) throws SQLException {
        int count = getCalibrateCount(user);
        String query = "UPDATE user_settings SET calibrate = ? WHERE user_id = ?";
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setInt(1, count + 1);
        stmt.setInt(2, user.getUserId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void insertUserBucketWeights(UserEntity user) throws SQLException {
        c.setAutoCommit(false);
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
            ins.setDouble(4, 0.0);
            ins.executeUpdate();
        }
        c.commit();
        c.setAutoCommit(true);
        rs.close();
    }

    public ProductEntity getRandomProduct() throws SQLException {
        String query = "SELECT pid FROM Product ORDER BY RANDOM() LIMIT 1";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return new ProductEntity(rs.getString("pid"));
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
        System.out.println("UserId: " + u.getUserId());
        System.out.println("ProductId: " + p.getPid());
        String pid = "";
        int bucketId = 0;
        String query = "SELECT pid FROM Bucket " +
                "  JOIN (SELECT bottomBucket, weight FROM Bucket_weight " +
                "  WHERE topBucket IN (SELECT DISTINCT bucket FROM Bucket " +
                "  WHERE pid = ?) " +
                "  AND user = ? ORDER BY weight DESC LIMIT "+limit+") AS w " +
                "  ON w.bottomBucket = bucket AND top = ? GROUP BY pid ORDER BY COUNT(pid)*w.weight LIMIT "+limit;
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setString(1, p.getPid());
        stmt.setInt(2, u.getUserId());
        stmt.setString(3, "bottom");
        ResultSet match = stmt.executeQuery();
        int row = 1 + (int)(Math.random() * (limit-1));
        int counter = 0;
        while (match.next() && counter != row) {
            System.out.println("is closed: " + match.isClosed());
            pid = match.getString("pid");
            //bucketId = match.getInt("bottomBucket");
            counter++;
        }
        // TODO this line gives SQLException: ResultSet closed ???
        //String pid = match.getString("pid");
        System.out.println("Bottombucket: " + bucketId);
        match.close();
        return new ProductEntity(pid);
    }

    public void update(UserEntity user, ProductEntity product, int like) throws SQLException {
        c.setAutoCommit(false);
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
        c.commit();
        c.setAutoCommit(true);
        rs.close();
    }

    public void updateMatch(UserEntity user, ProductEntity top, ProductEntity bottom,int like) throws SQLException {
        c.setAutoCommit(false);
        String buckets = "SELECT weight, topBucket, bottomBucket FROM Bucket_weight WHERE user = ? AND " +
                "topBucket IN (SELECT bucket FROM Bucket WHERE pid = ? AND top = 'top') AND " +
                "bottomBucket IN (SELECT bucket FROM Bucket WHERE pid = ? AND top = 'bottom')";
        PreparedStatement weightStmt = c.prepareStatement(buckets);
        weightStmt.setInt(1, user.getUserId());
        weightStmt.setString(2, top.getPid());
        weightStmt.setString(3, bottom.getPid());
        ResultSet rs = weightStmt.executeQuery();
        while (rs.next()) {
            String update = "UPDATE Bucket_weight " +
                    "SET weight = ? WHERE user = ? AND topBucket = ? AND bottomBucket = ?";
            PreparedStatement updateStmt = c.prepareStatement(update);
            updateStmt.setDouble(1, rs.getDouble("weight") + (like *ALPHA * Math.abs(like - rs.getDouble("weight"))));
            updateStmt.setInt(2, user.getUserId());
            updateStmt.setInt(3, rs.getInt("topBucket"));
            updateStmt.setInt(4, rs.getInt("bottomBucket"));
            updateStmt.executeUpdate();
        }
        c.commit();
        c.setAutoCommit(true);
        rs.close();
    }

    public void close() throws SQLException{
        c.close();
    }
}