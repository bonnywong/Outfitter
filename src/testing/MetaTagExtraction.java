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
    private static Connection c;

    private final static String IMAGES_PATH = "C:/Users/Filiz/Downloads/python-crawler/images/";
    private final static String BLANK_IMAGE_PATH = "C:/Users/Filiz/Downloads/python-crawler/images/0102164002.jpg";

    private static int dataSize = 0;



    public static void main(String[] args) {
        try {
            connect();
            getAllTags();
            reduceTagsInDB();
        } catch (Exception e) {
            System.err.println(e.getClass() + " " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:images.db");
    }

    private static void getAllTags() throws SQLException {
        // Id, Top, Gender, Metadata
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
        // TODO resize to lower resolution to speed up?
        if(pic1.getWidth() != pic2.getWidth() || pic1.getHeight() != pic2.getHeight()) return false;
        for(int w = 0; w < pic1.getWidth(); w++) {
            for(int h = 0; h < pic1.getHeight(); h++) {
                if(pic1.getRGB(w,h) != pic2.getRGB(w,h)) return false;
            }
        }
        return true;
    }

    private static void reduceTagsInDB() {
        try {
            String query = "SELECT Id, Metadata FROM Images";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            HashSet<Integer> nrOfTags = new HashSet<Integer>();
            while(rs.next()) {
                // TODO why didn't Id id or Identifier work?
                if(!unusedData.contains(rs.getString("Id"))) {
                    HashSet<String> usefulTags = choseUsefulTags(rs.getString("Metadata").split(" "));
                    nrOfTags.add(usefulTags.size());
                    // TODO insert tags to new DB
                    for(String tag : usefulTags) {
                        //System.out.println(tag);
                    }
                }
            }
            System.out.println("Number of unused clothes: " + unusedData.size());
            System.out.println("Max: "+ Collections.max(nrOfTags));
            System.out.println("Min: "+ Collections.min(nrOfTags));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static HashSet<String> choseUsefulTags(String[] metaTags) {
        HashSet<String> usefulTags = new HashSet<String>();
        for(String tag : metaTags) {
            if(tagCountMap.get(tag) > dataSize/(dataSize*20)) {
                usefulTags.add(tag);
            }
        }
        return usefulTags;
    }
}
