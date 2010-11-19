package javax.ejb;

public class RemoveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RemoveException() {
        super();
    }

    public RemoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoveException(String message) {
        super(message);
    }

    public RemoveException(Throwable cause) {
        super(cause);
    }
}
