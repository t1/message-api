package net.java.dev.messageapi.chat;

import java.io.IOException;

import javax.servlet.http.*;

import net.java.dev.messageapi.ChatApi;

import com.oneandone.consumer.messageapi.adapter.*;
import com.oneandone.consumer.messageapi.adapter.xml.JmsXmlSenderFactory;

public class ChatSend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// params are factory, destination, user, password
	private static final JmsConfig CONFIG = DefaultJmsConfigFactory.getJmsConfig("java:/JmsXA",
			"topic/testTopic", "guest", "guest");

	private final ChatApi chat = JmsXmlSenderFactory.createProxy(ChatApi.class, CONFIG);

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String message = request.getParameter("message");
		chat.send(message);

		response.sendRedirect("index.jsp");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		processRequest(request, response);
	}
}
