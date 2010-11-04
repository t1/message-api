package net.java.messageapi.adapter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AdapterMethod2 {

    @javax.xml.bind.annotation.XmlElement(required = true)
    private final int aliasedInt;

    @SuppressWarnings("unused")
    private AdapterMethod2() {
        this.aliasedInt = 0;
    }

    public AdapterMethod2(int i) {
        this.aliasedInt = i;
    }

    public int getAliasedInt() {
        return aliasedInt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdapterMethod2 other = (AdapterMethod2) obj;
        if (aliasedInt != other.aliasedInt)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleMethod2(" + aliasedInt + ")";
    }
}
