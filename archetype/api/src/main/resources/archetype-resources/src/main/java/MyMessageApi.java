package ${package};

import net.java.messageapi.MessageApi;

/** this is just a sample message api */
@MessageApi
public interface MyMessageApi {
	public void send(String message);
}
