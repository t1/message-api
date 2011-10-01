package javax.jms;

public class JMSException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JMSException() {
        super();
    }

    public JMSException(String message, Throwable cause) {
        super(message, cause);
    }

    public JMSException(String message) {
        super(message);
    }

    public JMSException(Throwable cause) {
        super(cause);
    }
}
