package net.java.messageapi.tck.simple;

import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.MessageListener;

import net.java.messageapi.adapter.MessageDecoder;

@MessageDriven(messageListenerInterface = MessageListener.class, //
activationConfig = { //
@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test" // "net.java.messageapi.tck.simple.SimpleApi"
) })
public class SimpleMDB extends MessageDecoder<SimpleApi> implements SimpleApi {
    @Inject
    private ResultWatcher resultWatcher;

    @Override
    public void simpleMethod(String simpleArg) {
        System.out.println("---------- receive: " + simpleArg);
        sleep();
        resultWatcher.invoke(simpleArg);
        System.out.println("---------- done: " + simpleArg);
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
