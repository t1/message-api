package jmskata.send;

import java.io.*;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import jmskata.messaging.CustomerService;

@WebServlet("/sender")
public class SenderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    private CustomerService customerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        reply(response, "create/delete customer (2)", "" //
                + "    <hr/>\n"//
                + "    <h3>Please enter the name of the customer to be created</h3>\n"
                + "    <form method=\"post\" action=\"sender\"><p/>\n"
                + "      <input type=\"text\" name=\"first\" size=\"50\"/><br/>\n"
                + "      <input type=\"text\" name=\"last\" size=\"50\"/><br/>\n"
                + "      <input type=\"hidden\" name=\"action\" value=\"create\"/>\n"
                + "      <input type=\"submit\" value=\"Create\" />\n" //
                + "    </form>\n"//
                + "    <hr/>\n"//
                + "    <h3>Please enter the id of the customer to be deleted</h3>\n"
                + "    <form method=\"post\" action=\"sender\"><p/>\n"
                + "      <input type=\"text\" name=\"id\" size=\"50\"/><br/>\n"
                + "      <input type=\"hidden\" name=\"action\" value=\"delete\"/>\n"
                + "      <input type=\"submit\" value=\"Delete\" />\n" //
                + "    </form>\n" //
                + "    <hr/>\n"//
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            String first = request.getParameter("first");
            String last = request.getParameter("last");

            customerService.createCustomer(first, last);

            reply(response, "customer created", "first=" + first + "<br/>" + "last=" + last);
        } else if ("delete".equals(action)) {
            String id = request.getParameter("id");

            customerService.deleteCustomer(id);

            reply(response, "customer deleted", "id=" + id);
        } else {
            reply(response, "error", "unknown action " + action);
        }
    }

    private void reply(HttpServletResponse response, String title, String body) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>");
            out.print(title);
            out.println("</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>");
            out.print(title);
            out.println("</h2>");
            out.print(body);
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
}
