package stockquote;

import javax.enterprise.event.Observes;

import net.java.messageapi.JmsIncoming;

public class Trader {
    void receive(@Observes @JmsIncoming StockQuote quote) {
        System.out.println("received " + quote);
    }
}
