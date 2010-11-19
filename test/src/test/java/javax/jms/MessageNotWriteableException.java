package javax.jms;

public class MessageNotWriteableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MessageNotWriteableException() {
        super();
    }

    public MessageNotWriteableException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageNotWriteableException(String message) {
        super(message);
    }

    public MessageNotWriteableException(Throwable cause) {
        super(cause);
    }
}
