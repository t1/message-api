package javax.jms;

public class MessageFormatException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MessageFormatException() {
        super();
    }

    public MessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageFormatException(String message) {
        super(message);
    }

    public MessageFormatException(Throwable cause) {
        super(cause);
    }
}
