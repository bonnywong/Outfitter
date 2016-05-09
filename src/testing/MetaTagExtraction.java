package testing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Attempting to extract the relevant metatags from the images.db
 * file.
 *
 * Created by swebo_000 on 2016-04-27.
 */
public class MetaTagExtraction {

    private static HashMap<String, Integer> tagCountMap = new HashMap<String, Integer>();
    private static HashSet<String> unusedData = new HashSet<String>();
    private static HashMap<String, Integer> usedTags = new HashMap<String, Integer>();

    // TODO replace file locations
    private final static String IMAGES_PATH = "C:/Users/Filiz/Downloads/python-crawler/images/";
    private final static String BLANK_IMAGE_PATH = "C:/Users/Filiz/Downloads/python-crawler/images/0102164002.jpg";

    private static int dataSize = 0;
    private static int tagId = 0;

    public static void main(String[] args) {
        try {
            Connection c = connect("jdbc:sqlite:images.db");
            getAllTags(c);
            reduceTagsInDB(c);
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass() + " " + e.getMessage());
            System.exit(-1);
        }
    }

    private static Connection connect(String db) throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(db);
    }

    private static void getAllTags(Connection c) throws SQLException {
        String query = "SELECT Id, Metadata FROM Images";
        Statement stmt = c.createStatement();
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
    }

    private static boolean usingPicture(String picName) {
        try {
            String thisImagePath = IMAGES_PATH + picName + ".jpg";
            BufferedImage thisImage = ImageIO.read(new File(thisImagePath));
            BufferedImage blankImage = ImageIO.read(new File(BLANK_IMAGE_PATH));
            if(sameImage(thisImage, blankImage)) {
                unusedData.add(picName);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private static void reduceTagsInDB(Connection c) throws SQLException, ClassNotFoundException {
        String query = "SELECT Id, Metadata, Top, Gender FROM Images";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        HashSet<Integer> nrOfTags = new HashSet<Integer>();
        Connection outfitter = connect("jdbc:sqlite:outfitterdb.db");
        while(rs.next()) {
            if(!unusedData.contains(rs.getString("Id"))) {
                HashSet<String> usefulTags = choseUsefulTags(rs.getString("Metadata").split(" "));
                nrOfTags.add(usefulTags.size());
                String pid = rs.getString("Id");
                insert(outfitter, "insert into product values('" + pid + "','" + rs.getString("Top") + "','" + rs.getString("Gender") + "');");
                for(String tag : usefulTags) {
                    addProductTag(outfitter, tag, pid);
                }
            }
        }
        outfitter.close();
        System.out.println("Number of unused clothes: " + unusedData.size());
        System.out.println("Max: "+ Collections.max(nrOfTags));
        System.out.println("Min: "+ Collections.min(nrOfTags));
    }

    private static void addProductTag(Connection c, String tag, String pid) {
        if(usedTags.get(tag) == null) {
            usedTags.put(tag, tagId++);
            insert(c, "insert into tags values(" + tagId + ",'" + tag +"');");
        }
        insert(c, "insert into productTag values('" + pid + "'," + usedTags.get(tag) + ");");
    }

    private static void insert(Connection c, String query) {
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
