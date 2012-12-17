package chat;

import java.io.*;

import javax.inject.Inject;
import javax.servlet.http.*;

import net.java.messageapi.DestinationName;

public class ChatSend extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    @DestinationName("queue/test")
    ChatApi chat;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // we don't expect any GET parameters.
        response(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request(request);
        response(response);
    }

    private void request(HttpServletRequest request) {
        String message = request.getParameter("message");
        if (message != null && message.length() > 0) {
            chat.send(message);
        }
    }

    private void response(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("  <head><title>Chat Send</title></head>");
        out.println("  <body>");
        out.println("    <h2>Please enter your message</h2>");
        out.println("    <form method=\"post\" action=\"chat-send\"><p/>");
        out.println("      <input type=\"text\" name=\"message\" size=\"50\"/><br/>");
        out.println("      <input type=\"submit\" value=\"Send\" />");
        out.println("    </form>");
        out.println("  </body>");
        out.println("</html>");
    }
}
