package net.java.messageapi.adapter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AdapterMethod3 {

    @javax.xml.bind.annotation.XmlElement(required = true)
    private final int i;
    @javax.xml.bind.annotation.XmlElement(required = true)
    private final java.lang.String s;

    @SuppressWarnings("unused")
    private AdapterMethod3() {
        this.i = 0;
        this.s = null;
    }

    public AdapterMethod3(int i, String s) {
        this.i = i;
        this.s = s;
    }

    public int getI() {
        return i;
    }

    public java.lang.String getS() {
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdapterMethod3 other = (AdapterMethod3) obj;
        if (i != other.i)
            return false;
        if (s == null) {
            if (other.s != null)
                return false;
        } else if (!s.equals(other.s))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "SimpleMethod3(" + i + ", " + s + ")";
    }
}
