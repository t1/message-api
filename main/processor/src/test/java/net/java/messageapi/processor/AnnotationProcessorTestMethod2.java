package net.java.messageapi.processor;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AnnotationProcessorTestMethod2 {

    @javax.xml.bind.annotation.XmlElement(required = true)
    private final int aliasedInt;

    @SuppressWarnings("unused")
    private AnnotationProcessorTestMethod2() {
        this.aliasedInt = 0;
    }

    public AnnotationProcessorTestMethod2(int i) {
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
        AnnotationProcessorTestMethod2 other = (AnnotationProcessorTestMethod2) obj;
        if (aliasedInt != other.aliasedInt)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleMethod2(" + aliasedInt + ")";
    }
}
