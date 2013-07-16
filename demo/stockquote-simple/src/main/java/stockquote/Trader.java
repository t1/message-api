package stockquote;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

import net.java.messageapi.JmsIncoming;

@Stateless
public class Trader {
    // TODO send qualifier and select here
    public void receive(@Observes @JmsIncoming StockQuote quote) {
        System.out.println("##### received " + quote);
    }
}
