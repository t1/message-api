package net.java.messageapi.chat;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

public class ChatReceiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("  <head>");
		out.println("    <meta http-equiv=\"refresh\" content=\"1\" >");
		out.println("    <title>Chat Receive</title>");
		out.println("  </head>");
		out.println("  <body>");
		out.println("    <h2>Messages Received</h2>");
		out.println("    <ul>");
		if (ChatReceiver.messages.isEmpty()) {
			out.append("      <li><i>no messages received, yet</i></li>\n");
		}
		for (String message : ChatReceiver.messages) {
			out.append("      <li>").append(message).append("</li>\n");
		}
		out.println("    </ul>");
		out.println("    <small>Please refresh manually</small>");
		out.println("  </body>");
		out.println("</html>");
	}
}
