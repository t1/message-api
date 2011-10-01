package javax.jms;

public class MessageNotReadableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MessageNotReadableException() {
        super();
    }

    public MessageNotReadableException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageNotReadableException(String message) {
        super(message);
    }

    public MessageNotReadableException(Throwable cause) {
        super(cause);
    }
}
