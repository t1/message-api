package stockquote;

import javax.enterprise.event.Observes;

import net.java.messageapi.JmsIncoming;

public class Trader {
    // TODO send qualifier and select here
    void receive(@Observes @JmsIncoming StockQuote quote) {
        System.out.println("##### received " + quote);
    }
}
