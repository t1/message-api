package stockquote;

import javax.enterprise.event.Observes;

import net.java.messageapi.*;

public class Trader {
    // TODO make JmsSelector work
    void receive(@Observes @JmsIncoming @JmsSelector("symbol = 'ORCL'") StockQuote quote) {
        System.out.println("##### received " + quote);
    }
}
