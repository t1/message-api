package javax.ejb;

public class EJBException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EJBException() {
        super();
    }

    public EJBException(String message, Throwable cause) {
        super(message, cause);
    }

    public EJBException(String message) {
        super(message);
    }

    public EJBException(Throwable cause) {
        super(cause);
    }
}
