package stockquote;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.MessageListener;

import net.java.messageapi.JmsIncoming;
import net.java.messageapi.adapter.EventDecoder;

@MessageDriven(messageListenerInterface = MessageListener.class, //
        activationConfig = { @ActivationConfigProperty(propertyName = "destination",
                propertyValue = "stockquote.StockQuote") })
public class ORCLMDB extends EventDecoder<StockQuote> {
    public ORCLMDB() {
        super(null, null);
        throw new UnsupportedOperationException("default consturctor required by MDB lifecycle, but never called");
    }

    @Inject
    public ORCLMDB(@JmsIncoming Event<StockQuote> payload) {
        super(StockQuote.class, payload);
    }
}
