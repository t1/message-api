package stockquote;

import javax.enterprise.event.Observes;

import net.java.messageapi.*;

public class Trader {
    void receive(@Observes @JmsIncoming @JmsSelector("symbol = 'ORCL'") StockQuote quote) {
        System.out.println("received " + quote);
    }
}
