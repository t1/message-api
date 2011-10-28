package net.java.messageapi.adapter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AdapterMethod1 {

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "SimpleMethod1()";
    }
}