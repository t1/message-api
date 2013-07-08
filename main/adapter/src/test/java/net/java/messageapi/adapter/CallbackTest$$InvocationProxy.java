package net.java.messageapi.adapter;

import java.lang.reflect.Method;

/** experimental manual implementation */
public class CallbackTest$$InvocationProxy extends CallbackTest {
    private final InvocationProxy<CallbackTest> proxy;

    public CallbackTest$$InvocationProxy(InvocationProxy<CallbackTest> proxy) {
        this.proxy = proxy;
    }

    @Override
    public void customerCreated(long createdCustomerId) {
        try {
            Method method = CallbackTest.class.getMethod("customerCreated", Long.TYPE);
            proxy.invoke(method, new Object[] { createdCustomerId });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
