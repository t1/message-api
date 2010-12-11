package ${package};

import net.java.messageapi.MessageApi;

@MessageApi
public interface MyMessageApi {
	public void send(String message);
}
