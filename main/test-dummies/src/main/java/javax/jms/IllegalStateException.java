package javax.jms;

public class IllegalStateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IllegalStateException() {
        super();
    }

    public IllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalStateException(String message) {
        super(message);
    }

    public IllegalStateException(Throwable cause) {
        super(cause);
    }
}
