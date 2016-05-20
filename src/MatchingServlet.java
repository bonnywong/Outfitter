import Models.ProductEntity;
import Models.UserEntity;
import Models.UserSettingEntity;
import persist.AccountStore;
import testing.DatabaseHandler;
import testing.MatchingHelper;

import javax.jws.soap.SOAPBinding;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * Created by swebo_000 on 2016-04-16.
 */
public class MatchingServlet extends HttpServlet {

    private DatabaseHandler dbHandler;

    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        String action = request.getParameter("action");
        UserEntity user = (UserEntity) request.getSession().getAttribute("user");
        int positive = 0;
        int negative = 0;
        if (request.getSession().getAttribute("positive") == null) {
            request.getSession().setAttribute("positive", positive);
        } else {
            positive = (Integer) request.getSession().getAttribute("positive");
        }

        if (request.getSession().getAttribute("negative") == null) {
            request.getSession().setAttribute("negative", negative);
        } else {
            negative = (Integer) request.getSession().getAttribute("negative");
        }

        ProductEntity topProduct = new ProductEntity();
        ProductEntity bottomProduct = new ProductEntity();
        HashSet<String> topProductTags = new HashSet<String>();
        HashSet<String> bottomProductTags = new HashSet<String>();

        //TODO: Should redirect to error page.
        try {
             dbHandler = new DatabaseHandler();
            if (dbHandler.getCalibrateCount(user) < 10) {
                System.out.println("Fetching random product!");
                topProduct = dbHandler.getRandomProduct();
                bottomProduct = dbHandler.findMatch(topProduct, user, 10);
                topProductTags = dbHandler.getMeta(topProduct);
                bottomProductTags = dbHandler.getMeta(bottomProduct);
                dbHandler.incrementCalibrateCount(user);
            } else {
                //Normal matching.
                System.out.println("Fetching regular!");
                dbHandler = new DatabaseHandler();
                topProduct = dbHandler.findProduct(user, "top", 5); //We only have top in db atm
                bottomProduct = dbHandler.findMatch(topProduct, user, 10);
                topProductTags = dbHandler.getMeta(topProduct);
                bottomProductTags = dbHandler.getMeta(bottomProduct);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }


        String topId = topProduct.getPid().substring(1); //Strip the leading 0.
        String botId = bottomProduct.getPid().substring(1);

        //System.out.println("Fetched product with ID: " + topId);
        request.getSession().setAttribute("topId", topId);
        request.getSession().setAttribute("botId", botId);
        request.getSession().setAttribute("topProductTags", topProductTags);
        request.getSession().setAttribute("bottomProductTags", bottomProductTags);

        if (action != null) {
            if (action.equals("like")) {
                System.out.println("Server: Pressed \"Like\" button");
                //request.getSession().setAttribute("topId", topId);
                updateWeights(user, topProduct, bottomProduct, 1);
                positive += 1;
                request.getSession().setAttribute("positive", positive);
            }
            if (action.equals("meh")) {
                System.out.println("Server: Pressed \"Meh\" button");
                updateWeights(user, topProduct, bottomProduct, 0);
            }
            if (action.equals("dislike")) {
                System.out.println("Server: Pressed \"Dislike\" button");
                updateWeights(user, topProduct, bottomProduct, -1);
                negative += 1;
                request.getSession().setAttribute("negative", negative);
            }
            System.out.println("Updating weights for: " + user.getUsername() + ". ID: " + user.getUserId());
        }

        try {
            RequestDispatcher rd = request.getRequestDispatcher("matching.jsp");
            rd.forward(request, response);
            dbHandler.close();
        } catch (ServletException e) {
            System.out.println("Servlet Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateWeights(UserEntity user, ProductEntity topProduct, ProductEntity bottomProduct, int like) {
        try {
            dbHandler.updateMatch(user, topProduct, bottomProduct, like);
            dbHandler.update(user, topProduct, like);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }

}
