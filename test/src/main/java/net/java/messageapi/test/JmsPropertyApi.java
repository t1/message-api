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

    public void jmsPropertyMethod(@JmsProperty String one, String two);

    public void jmsPropertyBooleanMethod(@JmsProperty Boolean one, String two);

    public void jmsPropertyPrimitiveBooleanMethod(@JmsProperty boolean one, String two);

    public void jmsPropertyIntMethod(@JmsProperty Integer one, String two);

    public void jmsPropertyPrimitiveIntMethod(@JmsProperty int one, String two);

    public void jmsPropertyLongMethod(@JmsProperty Long one, String two);

    public void jmsPropertyPrimitiveLongMethod(@JmsProperty long one, String two);

    public void jmsPropertyTwiceMethod(@JmsProperty String one, @JmsProperty String two);

    public void jmsPropertyMethodWithHeaderOnly(@JmsProperty(headerOnly = true) String one,
            String two);

    public void jmsPropertyInNestedClass(NestedAnnotated one, String two);
}
