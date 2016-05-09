import Models.UserEntity;
import testing.MatchingHelper;

import javax.jws.soap.SOAPBinding;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by swebo_000 on 2016-04-16.
 */
public class MatchingServlet extends HttpServlet {
    MatchingHelper helper = new MatchingHelper();
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        /**
         * Current logic: -> User enters JSP, JSP calls recommendation ->  ProductEntity/Images ->
         * Display them using specific ImageServlet -> Make sure to keep the ProductEntity in session -> dispatch it ->
         * Fetch it here in doGet() -> Update user.
         */

        String action = request.getParameter("action");
        UserEntity user = (UserEntity) request.getSession().getAttribute("user");
        int topId = 108775039;
        int botId = 179738012;

        request.getSession().setAttribute("topId", topId);
        request.getSession().setAttribute("botId", botId);

        if (action != null) {
            System.out.println("Top ID from JSP: " + request.getAttribute("idTop"));
            System.out.println("Bottom ID from JSP: " + request.getAttribute("idBot"));
            if (action.equals("like")) {
                System.out.println("Server: Pressed \"Like\" button");
                request.getSession().setAttribute("topId", botId);
                request.getSession().setAttribute("botId", topId);

                //TODO: Update weights for this specific user and fetch new clothing.
            }
            if (action.equals("meh")) {
                System.out.println("Server: Pressed \"Meh\" button");
                //TODO: Update weights for this specific user and fetch new clothing.
            }
            if (action.equals("dislike")) {
                System.out.println("Server: Pressed \"Dislike\" button");
                //TODO: Update weights for this specific user and fetch new clothing.
                request.getSession().setAttribute("topId", topId);
                request.getSession().setAttribute("botId", botId);
            }
            System.out.println("Updating weights for: " + user.getUsername() + ". ID: " + user.getUserId());
        }

        try {
            RequestDispatcher rd = request.getRequestDispatcher("matching.jsp");
            rd.forward(request, response);
        } catch (ServletException e) {
            System.out.println("Servlet Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }
}
