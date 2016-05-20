package testing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class MetaTagExtraction {

    private static HashMap<String, Integer> tagCountMap = new HashMap<String, Integer>();
    private static HashSet<String> unusedData = new HashSet<String>();
    private static HashMap<String, Integer> usedTags = new HashMap<String, Integer>(); //tag name + id I assume

//    private final static String IMAGES_PATH = "C:/Users/Filiz/Downloads/python-crawler/images/";
//    private final static String BLANK_IMAGE_PATH = "C:/Users/Filiz/Downloads/python-crawler/images/0102164002.jpg";
//    private final static String TOP_BUCKET = "C:/Users/Filiz/Downloads/topBuckets.txt";
//    private final static String BOTTOM_BUCKET = "C:/Users/Filiz/Downloads/bottomBuckets.txt";

    private final static String IMAGES_PATH = "C:/Users/swebo_000/Desktop/python-crawler/images/";
    private final static String BLANK_IMAGE_PATH = "C:/Users/swebo_000/Desktop/python-crawler/images/0102164002.jpg";
    private final static String TOP_BUCKET = "C:/Users/swebo_000/Downloads/topBuckets.txt";
    private final static String BOTTOM_BUCKET = "C:/Users/swebo_000/Downloads/bottomBuckets.txt";

    private static int dataSize = 0;
    private static int tagId = 0;

    private static Connection imagesCon;
    private static Connection outfitterCon;

    public static void main(String[] args) {
        try {
            imagesCon = connect("jdbc:sqlite:images.db");
            outfitterCon = connect("jdbc:sqlite:outfitterdb.db");

            outfitterCon.setAutoCommit(false);

            System.out.println("connected");
            getAllTags();
            System.out.println("alltags");

            long start = System.currentTimeMillis();

            System.out.println("Resetting tables...");
            resetTables(outfitterCon);

            System.out.println("Reducing tags in DB.");
            reduceTagsInDB();
            System.out.println("Reduced.");

            System.out.println("Building top-bucket");
            buildBucketDB(TOP_BUCKET, "top");
            System.out.println("Bucket built.");

            System.out.println("Building bottom-bucket");
            buildBucketDB(BOTTOM_BUCKET, "bottom");
            System.out.println("Bucket built.");

            outfitterCon.commit();
            imagesCon.close();
            outfitterCon.close();

            System.out.println("Time taken: " + (System.currentTimeMillis() - start) + "ms");

        } catch (Exception e) {
            System.err.println(e.getClass() + " " + e.getMessage());
            System.exit(-1);
        }
    }

    private static Connection connect(String db) throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(db);
    }

    private static void buildBucketDB(String filename, String top) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String bucket = parts[0];
                String[] pids = parts[1].split(" ");
                for(String pid : pids) {
                    if(!unusedData.contains(pid)) {
                        insert("INSERT INTO Bucket VALUES(" + bucket + ",'" + pid + "','" + top +"')");
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void getAllTags() throws SQLException, IOException {
        String query = "SELECT Id, Metadata FROM Images";
        Statement stmt = imagesCon.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            if (usingPicture(rs.getString("Id"))) {
                dataSize++;
                String[] metaTags = rs.getString("Metadata").split(" ");
                for (String tag : metaTags) {
                    if (tagCountMap.get(tag) == null) {
                        tagCountMap.put(tag, 1);
                    } else {
                        tagCountMap.put(tag, tagCountMap.get(tag) + 1);
                    }
                }
            }
        }
        stmt.close();
        rs.close();
    }

    private static boolean usingPicture(String picName) throws IOException {
        String thisImagePath = IMAGES_PATH + picName + ".jpg";
        BufferedImage thisImage = ImageIO.read(new File(thisImagePath));
        BufferedImage blankImage = ImageIO.read(new File(BLANK_IMAGE_PATH));
        if(sameImage(thisImage, blankImage)) {
            unusedData.add(picName);
            return false;
        }
        return true;
    }

    private static boolean sameImage(BufferedImage pic1, BufferedImage pic2) {
        // only comparing 9 pixels evenly separated in the pictures
        // works now but might need more if the image database is updated with new pictures
        if(pic1.getWidth() != pic2.getWidth() || pic1.getHeight() != pic2.getHeight()) return false;
        for(int w = 0; w < pic1.getWidth(); w += pic1.getWidth()/3) {
            for(int h = 0; h < pic1.getHeight(); h += pic1.getHeight()/3) {
                if(pic1.getRGB(w,h) != pic2.getRGB(w,h)) return false;
            }
        }
        return true;
    }

    private static void reduceTagsInDB() throws SQLException, ClassNotFoundException {
        String query = "SELECT Id, Metadata, Top, Gender FROM Images";
        Statement stmt = imagesCon.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        HashSet<Integer> nrOfTags = new HashSet<Integer>();
        while(rs.next()) {
            if(!unusedData.contains(rs.getString("Id"))) {
                HashSet<String> usefulTags = choseUsefulTags(rs.getString("Metadata").split(" "));
                nrOfTags.add(usefulTags.size());
                String pid = rs.getString("Id");
                insert("insert into product values('" + pid + "','" + rs.getString("Top") + "');");
                addProductTag(rs.getString("Gender"), pid);
                for(String tag : usefulTags) {
                    addProductTag(tag, pid);
                }
            }
        }
        stmt.close();
        rs.close();
    }

    private static void addProductTag(String tag, String pid) {
        if(usedTags.get(tag) == null) {
            usedTags.put(tag, tagId);
            insert("insert into tags values(" + tagId + ",'" + tag +"');");
            tagId++;
        }
        insert("insert into productTag values('" + pid + "'," + usedTags.get(tag) + ");");
    }

    private static void insert(String query) {
        try {
            Statement stmt = outfitterCon.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void resetTables(Connection c) throws SQLException{
        String product = "DELETE FROM Product";
        String productTag = "DELETE FROM productTag";
        String tags = "DELETE FROM tags";
        String bucket = "DELETE FROM Bucket";
        Statement stmt = c.createStatement();
        stmt.executeUpdate(product);
        stmt.executeUpdate(productTag);
        stmt.executeUpdate(tags);
        stmt.executeUpdate(bucket);
        stmt.close();
    }

    private static HashSet<String> choseUsefulTags(String[] metaTags) {
        HashSet<String> usefulTags = new HashSet<String>();
        for(String tag : metaTags) {
            if(metaTags.length < 15 || tagCountMap.get(tag) > dataSize/20) {
                usefulTags.add(tag);
            }
        }
        return usefulTags;
    }

    public void getTagsFile(Connection c) throws Exception {
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
}