package jmskata.send;

import java.io.*;

import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/sender")
public class SenderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "java:/jmskata.messaging.CustomerService")
    private Destination destination;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        reply(response, "create/delete customer (0)", "" //
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

            Connection connection = null;

            try {
                connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(destination);
                MapMessage message = session.createMapMessage();

                message.setStringProperty("action", "CREATE");
                message.setString("first", first);
                message.setString("last", last);

                messageProducer.send(message);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException e) {
                        // ignore
                    }
                }
            }

            reply(response, "customer created", "first=" + first + "<br/>" + "last=" + last);
        } else if ("delete".equals(action)) {
            String id = request.getParameter("id");

            Connection connection = null;

            try {
                connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(destination);
                MapMessage message = session.createMapMessage();

                message.setStringProperty("action", "DELETE");
                message.setString("id", id);

                messageProducer.send(message);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException e) {
                        // ignore
                    }
                }
            }

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
