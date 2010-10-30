package net.java.dev.messageapi.chat;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

import net.java.dev.messageapi.ChatApi;

import com.oneandone.consumer.messageapi.adapter.DefaultJmsConfigFactory;
import com.oneandone.consumer.messageapi.adapter.JmsConfig;
import com.oneandone.consumer.messageapi.adapter.xml.JmsXmlSenderFactory;

public class ChatSend extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // params are factory, destination, user, password
    private static final JmsConfig CONFIG = DefaultJmsConfigFactory.getJmsConfig(
            "ConnectionFactory", "ChatTopic", "guest", "guest");

    private final ChatApi chat = JmsXmlSenderFactory.createProxy(ChatApi.class, CONFIG);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // we're not expecting any GET parameters!
        response(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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
