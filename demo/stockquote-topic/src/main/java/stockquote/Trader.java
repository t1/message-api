package stockquote;

import javax.enterprise.event.Observes;

import net.java.messageapi.*;

public class Trader {
    void receive(@Observes @JmsIncoming StockQuote quote) {
        System.out.println("##### received " + quote);
    }
}
