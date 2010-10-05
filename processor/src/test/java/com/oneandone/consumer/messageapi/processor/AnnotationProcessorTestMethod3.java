package com.oneandone.consumer.messageapi.processor;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AnnotationProcessorTestMethod3 {

    @javax.xml.bind.annotation.XmlElement(required = true)
    private final int i;
    @javax.xml.bind.annotation.XmlElement(required = true)
    private final java.lang.String s;

    @SuppressWarnings("unused")
    private AnnotationProcessorTestMethod3() {
        this.i = 0;
        this.s = null;
    }

    public AnnotationProcessorTestMethod3(int i, String s) {
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
        AnnotationProcessorTestMethod3 other = (AnnotationProcessorTestMethod3) obj;
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
    public String toString() {
        return "SimpleMethod3(" + i + ", " + s + ")";
    }
}
