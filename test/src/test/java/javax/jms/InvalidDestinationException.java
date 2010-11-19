package javax.jms;

public class InvalidDestinationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidDestinationException() {
        super();
    }

    public InvalidDestinationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDestinationException(String message) {
        super(message);
    }

    public InvalidDestinationException(Throwable cause) {
        super(cause);
    }
}
