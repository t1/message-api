# Chat-Demo

This is a step-by-step guide to create a simple chat application.
It consists of a sender to post messages you type to a JMS Topic and a receiver to display all messages coming in from that topic.
Both are implemented as simple servlets.
Most of the things in it could have to be done with plain JMS as well.
But if you know JMS, you'll see the big difference in the code you ''don't'' have to write.
If you prefer to download everything, you can get it [http://java.net/projects/messageapi/sources/svn/show/tags/1.1/demo here].
To make the interesting things more visible, the stuff that's not specific to the MessageApi but just to make the servlets work, is set in <span style="color:grey">grey</span>.
You can skip that, if you just want to get an impression of how to work with the MessageApi.

<small style="color:green">Things we are working on to make it even easier, are set in green and small.</small>

This demo is done with maven 2.2.1 and Glassfish 3.0.1. But it should be straight forward to adapt to any container supporting JMS and to any build tool.

# Build the MessageApi
<ol>
<li><tt>svn co <nowiki>https://svn.java.net/svn/messageapi~svn/tags/1.1</nowiki> messageapi</tt></li>
<li><tt>cd messageapi</tt></li>
<li><tt>mvn clean install</tt>
<br/><small style="color:green">we are working on getting the messageapi into a maven repository, so you could skip this step ([http://java.net/jira/browse/MESSAGEAPI-1 MESSAGEAPI-1]).</small></li>
</ol>

# Create the Chat-API
<ol>
<li><tt>mvn archetype:create -DgroupId=chat -DartifactId=chat-api -DarchetypeGroupId=net.java.messageapi -DarchetypeArtifactId=api-archetype -DarchetypeVersion=1.1 -DremoteRepositories=<nowiki>http://download.java.net/maven/2</nowiki></tt></li>
<li>replace the sample <tt>MyMessageApi.java</tt> with your own <tt>ChatApi.java</tt>:
<pre name="java">@MessageApi
public interface ChatApi {
    public void send(String message);
}</pre>
</li>
<li><tt>mvn clean install</tt></li>
</ol>

# Create the Sender
<ol>
<li style="color:grey">create a web project: <tt>mvn archetype:generate -B -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeArtifactId=webapp-javaee6 -DgroupId=chat -DartifactId=chat-send -Dpackage=chat</tt></li>
<li>add dependencies to your <tt>chat-api</tt>, to <tt>joda-time:1.6.2</tt>, and the <tt>net.java.messageapi:adapter:1.1</tt></li>
<li style="color:grey">remove the file <tt>src/main/webapp/index.jsp</tt></li>
<li style="color:grey">create the folder <tt>src/main/webapp/WEB-INF</tt></li>
<li style="color:grey">create the file <tt>web.xml</tt> in that folder with this contents:<pre name="xml">
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
    <servlet>
    <servlet-name>ChatSend</servlet-name>
    <servlet-class>chat.ChatSend</servlet-class>
    </servlet>
    <servlet-mapping>
    <servlet-name>ChatSend</servlet-name>
    <url-pattern>/chat-send</url-pattern>
    </servlet-mapping>
    <welcome-file-list>
    <welcome-file>chat-send</welcome-file>
    </welcome-file-list>
</web-app>
</pre></li>
<li>create the <tt>chat.ChatSend.java</tt> (the imports and stuff are left out for brevity):
<pre name="java">
public class ChatSend extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ChatApi chat = MessageSender.of(ChatApi.class);

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
</pre></li>
<li>Create the configuration file <tt>chat.ChatApi.config</tt>:
<pre name="xml">
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<jmsSenderFactory api="net.java.messageapi.test.TestApi">
    <destination name="Chat">
        <factory>ConnectionFactory</factory>
        <user>guest</user>
        <pass>guest</pass>
        <transacted>true</transacted>
    </destination>
    <xmlJmsPayloadHandler/>
</jmsSenderFactory>
</pre>
<small style="color:green">we are working on a automatic registry for services, so this config would be unnecessary ([http://java.net/jira/browse/MESSAGEAPI-2 MESSAGEAPI-2]).</small></li>
<li style="color:grey">build the war: <tt>mvn clean verify</tt></li>
</ol>

# Create the Receiver
<ol>
<li style="color:grey">create another web project: <tt>mvn archetype:generate -B -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeArtifactId=webapp-javaee6 -DgroupId=chat -DartifactId=chat-<b>receive</b> -Dpackage=chat</tt></li>
<li style="color:grey">change the maven dependency on <tt>javaee-web-api</tt> to the full <tt>javaee-api</tt>; we'll add a MDB</li>
<li>add dependencies to your <tt>chat-api</tt> and the <tt>net.java.messageapi:adapter:1.1</tt></li>
<li style="color:grey">remove the file <tt>src/main/webapp/index.jsp</tt></li>
<li style="color:grey">create the folder <tt>src/main/webapp/WEB-INF</tt></li>
<li style="color:grey">create the file <tt>web.xml</tt> in that folder with this contents:<pre name="xml">
<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
    <servlet>
    <servlet-name>ChatReceive</servlet-name>
    <servlet-class>chat.ChatReceiveServlet</servlet-class>
    </servlet>
    <servlet-mapping>
    <servlet-name>ChatReceive</servlet-name>
    <url-pattern>/chat-receive</url-pattern>
    </servlet-mapping>
    <welcome-file-list>
    <welcome-file>chat-receive</welcome-file>
    </welcome-file-list>
</web-app>
</pre></li>
<li>create the <tt>chat.ChatReceiver</tt>:
<pre name="java">
public class ChatReceiver implements ChatApi {
    static List<String> messages = new ArrayList<String>();

    @Override
    public void send(String message) {
        ChatReceiver.messages.add(message);
    }

    public List<String> getMessages() {
        return ChatReceiver.messages;
    }

    public void setMessages(List<String> messages) {
        ChatReceiver.messages = messages;
    }
}
</pre></li>
<li>create the <tt>chat.ChatMDB</tt>:<pre name="java">
@MessageDriven(mappedName = "Chat")
public class ChatMdb extends XmlMessageDecoder<ChatApi> {
    public ChatMdb() {
        super(ChatApi.class, new ChatReceiver());
    }
}
</pre>
<small style="color:green">we are working on a generic MDB, so you only have to configure the connection between a destination and your business bean ([http://java.net/jira/browse/MESSAGEAPI-3 MESSAGEAPI-3])</small></li>
<li style="color:grey">create the <tt>chat.ChatReceiveServlet</tt>:
<pre name="java">
public class ChatReceiveServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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
        out.println("  </body>");
        out.println("</html>");
    }
}
</pre></li>
<li style="color:grey">build the war: <tt>mvn clean verify</tt></li>
</ol>

# Deploy on your glassfish
<span style="color:grey">
#open the <a href="http://localhost:4848">admin console</a>
#create the connection factory <tt>jms/ConnectionFactory</tt>
##go to <tt>Resources/JMS Resources/Connection Factories</tt>
##click <tt>New...</tt>
##set the <tt>Pool Name</tt> <tt>ConnectionFactory</tt>
##set the <tt>Resource Type</tt> <tt>javax.jms.ConnectionFactory</tt>
##hit return
#create the topic <tt>Chat</tt>
##go to <tt>Resources/JMS Resources/Destination Resources</tt>
##click <tt>New...</tt>
##set the <tt>JNDI Name</tt> and the <tt>Physical Destination Name</tt> to <tt>Chat</tt>
##hit return
#deploy the apps
##go to <tt>Applications</tt>
##click <tt>Deploy...</tt>
##browse for your <tt>chat-send.war</tt>
##hit <tt>continue</tt>
##wait until the deploy is finished
##repeat for the <tt>chat-receive.war</tt>
#open the <a href="http://localhost:8080/chat-send">sender</a>
#open the <a href="http://localhost:8080/chat-receive">receiver</a>
</span>

