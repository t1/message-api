package stockquote;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import net.java.messageapi.*;

@XmlRootElement
@MessageEvent
public class StockQuote implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlTransient
    @DynamicDestinationName
    private final String symbol;

    @XmlElement(required = true)
    private final BigDecimal price;

    @SuppressWarnings("unused")
    private StockQuote() {
        this.symbol = null;
        this.price = null;
    }

    public StockQuote(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StockQuote other = (StockQuote) obj;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "StockQuote(" + symbol + ", " + price + ")";
    }
}