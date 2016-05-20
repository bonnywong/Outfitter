import javassist.bytecode.ByteArray;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * This servlet only serves JPG image files.
 *
 * Created by swebo_000 on 2016-05-06.
 */
public class ImageServlet extends HttpServlet {

    private final String IMAGES_DIR = "C:/Users/swebo_000/Desktop/python-crawler/images/";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String imageId = request.getPathInfo().substring(1);
        String imagePath = IMAGES_DIR + imageId + ".jpg";

        File imageFile = new File(imagePath);

        FileInputStream in = new FileInputStream(imageFile);
        OutputStream out = response.getOutputStream();

        response.setContentType("image/jpg"); //Note, we're hardcoding the mime type.
        response.setContentLength((int)imageFile.length());

        byte[] buf = new byte[1024];
        int count;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        out.close();
        in.close();
    }
}
