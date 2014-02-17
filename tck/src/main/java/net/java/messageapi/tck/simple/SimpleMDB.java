package net.java.messageapi.tck.simple;

import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.MessageListener;

import net.java.messageapi.adapter.MessageDecoder;

@MessageDriven(messageListenerInterface = MessageListener.class, //
activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "jmskata.messaging.CustomerService") })
public class SimpleMDB extends MessageDecoder<SimpleApi> implements SimpleApi {
    @Inject
    private ResultWatcher resultWatcher;

    @Override
    public void simpleMethod(String simpleArg) {
        resultWatcher.invoke();
    }
}
