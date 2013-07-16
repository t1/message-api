package net.java.messageapi.adapter;

import java.lang.reflect.*;
import java.util.concurrent.*;

import javax.inject.Inject;
import javax.naming.*;

import org.slf4j.*;

/**
 * Executes a method call asynchronously and invokes a callback method with the result. For example:
 * 
 * <pre>
 * interface CustomerService {
 *     long createCustomer(String first, String last);
 * }
 * 
 * class Client {
 *     CustomerService service = Callback.forService(realService);
 * 
 *     void main() {
 *         Callback.replyTo(this).customerCreated(service.createCustomer(&quot;Joe&quot;, &quot;Doe&quot;));
 *     }
 * 
 *     void customerCreated(long newCustomerId) {
 *         ...
 *     }
 * }
 * </pre>
 * 
 * If the called method returns <code>void</code> (i.e. the callback method can't take any arguments), but you still
 * want the callback, when the asynchronous call was successful, then you'll have to do the call in a separate line. For
 * example, if <code>createCustomer</code> would return <code>void</code>, your code would look like this:
 * 
 * <pre>
 * void main() {
 *     service.createCustomer(&quot;Joe&quot;, &quot;Doe&quot;);
 *     Callback.replyTo(this).customerCreated();
 * }
 * 
 * void customerCreated() {
 *     ...
 * }
 * </pre>
 * 
 * TODO add exception callback handling
 * <p>
 * TODO improve error reporting for illegal number of argument combinations
 */
public class Callback {
    private static final Logger log = LoggerFactory.getLogger(Callback.class);
    private static final ThreadLocal<Callback> CALL_INFO = new ThreadLocal<Callback>();

    /**
     * Creates an instance of that type, storing all (proxyable) method calls so that they can be retrieved in
     * {@link Callback#replyTo(Object)}.
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
        }.newInstance();
    }

    /**
     * Picks up the call that was passed to the proxy created with {@link #forService(Object)}, invokes it
     * asynchronously, and replies to the target object passed in with the method called on the proxy returned by this
     * method. Sounds confusing? It's actually quite simple if you look at the example above.
     */
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
        }.newInstance();
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
        info.insert(0, method.getName());
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
        if (executor == null) {
            try {
                executor = (Executor) new InitialContext().lookup("java:module/Executor");
            } catch (NamingException e) {
                executor = Executors.newCachedThreadPool();
            }
        }
        return executor;
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
        boolean hasArgs = callbackMethod.getParameterTypes().length > 0;
        printMethod("reply", callbackMethod, hasArgs ? new Object[] { result } : new Object[0]);
        try {
            if (hasArgs) {
                callbackMethod.invoke(callbackTarget, result);
            } else {
                callbackMethod.invoke(callbackTarget);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
