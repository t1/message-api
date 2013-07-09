package net.java.messageapi.adapter;

import java.lang.reflect.*;
import java.util.concurrent.*;

import javax.ejb.*;
import javax.inject.Inject;
import javax.naming.*;

import org.slf4j.*;

/**
 * Executes a method call asynchronously and invokes a callback method with the result. For example:
 * 
 * <pre>
 * @AsynchronousService
 * interface CustomerService {
 *     long createCustomer(String first, String last);
 * }
 * 
 * import static net.java.messageapi.adapter.Callback.*;
 * 
 * class Client {
 *     @Inject
 *     CustomerService service;
 *     
 *     void main() {
 *         replyTo(this).customerCreated(service.createCustomer("Joe", "Doe"));
 *     }
 *     
 *     void customerCreated(long newCustomerId) {
 *         ...
 *     }
 * }
 * </pre>
 * 
 * TODO AsynchronousService annotation and an according Producer
 */
public class Callback {
    private static final Logger log = LoggerFactory.getLogger(Callback.class);
    private static final ThreadLocal<Callback> CALL_INFO = new ThreadLocal<Callback>();

    /**
     * @see http://stackoverflow.com/questions/13932083/jboss-java-ee-container-and-an-executorservice
     */
    @Stateless(name = "Executor")
    public static class ExecutorBean implements Executor {
        @Override
        @Asynchronous
        public void execute(Runnable command) {
            command.run();
        }
    }

    /**
     * Creates an instance of that type
     */
    public static <T> T forService(final T target) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) target.getClass();
        return new InvocationProxy<T>(type) {
            @Override
            public Object invoke(Method method, Object... args) {
                printMethod("store call info", method, args);
                storeCallbackInfo(target, method, args);
                return 0L;
            }

            private void storeCallbackInfo(Object target, Method method, Object[] args) {
                if (CALL_INFO.get() != null)
                    throw new IllegalStateException("call_info already set");
                Callback callback = new Callback(target, method, args);
                CALL_INFO.set(callback);
            }
        }.cast();
    }

    public static <T> T replyTo(final T target) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) target.getClass();
        return new InvocationProxy<T>(type) {
            @Override
            public Object invoke(Method method, Object... args) {
                final Callback callback = CALL_INFO.get();
                CALL_INFO.set(null);
                callback.invokeAndReplyTo(target, method);
                return null; // this is never used... the actual reply is asynchronous
            }
        }.cast();
    }

    public static boolean hasCallbackInfo() {
        return CALL_INFO.get() != null;
    }

    private static void printMethod(String prefix, Method method, Object[] args) {
        if (!log.isDebugEnabled())
            return;
        StringBuilder info = new StringBuilder();
        for (Object arg : args) {
            if (info.length() > 0)
                info.append(", ");
            info.append(arg);
        }
        info.insert(0, "(");
        // info.insert(0, method.getName());
        info.append(")");
        log.debug("{} {}", prefix, info);
    }

    private final Object target;
    private final Method method;
    private final Object[] args;

    private Callback(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    @Inject
    static Executor executor;

    private void invokeAndReplyTo(final Object callbackTarget, final Method callbackMethod) {
        executor().execute(new Runnable() {
            @Override
            public void run() {
                Object result = invoke();
                callback(callbackTarget, callbackMethod, result);
            }
        });
    }

    /**
     * If the @Inject via the {@link ExecutorBean} doesn't work, we'll try to look it up in JNDI... and if that doesn't
     * work either, we'll fall back to a cached thread pool (which should be okay, as we then seem to be outside
     * JavaEE).
     */
    private Executor executor() {
        if (executor == null)
            executor = lookupExecutor();
        return executor;
    }

    private Executor lookupExecutor() {
        try {
            return (Executor) new InitialContext().lookup("java:module/Executor");
        } catch (NamingException e) {
            return Executors.newCachedThreadPool();
        }
    }

    private Object invoke() {
        printMethod("invoke", method, args);
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("while calling " + method + " on " + target, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("while calling " + method + " on " + target, e);
        }
    }

    private void callback(Object callbackTarget, Method callbackMethod, Object result) {
        printMethod("reply", callbackMethod, new Object[] { result });
        try {
            callbackMethod.invoke(callbackTarget, result);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
