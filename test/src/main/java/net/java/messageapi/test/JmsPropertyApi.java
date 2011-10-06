package net.java.messageapi.test;

import javax.xml.bind.annotation.XmlElement;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.MessageApi;

@MessageApi
public interface JmsPropertyApi {
    public static class NestedAnnotated {
        @XmlElement
        @JmsProperty
        String nested;
    }

    public void methodWithProperty(@JmsProperty String one, String two);

    public void methodWithHeaderOnlyProperty(@JmsProperty(headerOnly = true) String one, String two);

    public void methodWithNestedProperty(NestedAnnotated one, String two);
}
