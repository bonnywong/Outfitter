package testing;

/**
 * Created by swebo_000 on 2016-05-06.
 */
public class MatchingHelper {

    private final String IMAGES_DIR = "C:/Users/swebo_000/Desktop/python-crawler/images/";

    //Is this actually enough to display images, or does this only work locally?
    public String getImagePath(String imageId) {
        return IMAGES_DIR + imageId + ".jpg";
    }

    public byte[] getImageBytes(String imageId) {
        String imagePath = IMAGES_DIR + imageId + ".jpg";

        return null;
    }
}
