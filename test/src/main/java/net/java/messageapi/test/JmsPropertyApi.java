package net.java.messageapi.test;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;

import net.java.messageapi.JmsProperty;
import net.java.messageapi.MessageApi;

@MessageApi
public interface JmsPropertyApi {
    public static class NestedAnnotated implements Serializable {
        private static final long serialVersionUID = 1L;

        @XmlElement
        @JmsProperty
        String nested;
    }

    public static class Cyclic {
        @XmlElement
        @JmsProperty
        Cyclic cycle;
    }

    public void jmsPropertyMethod(@JmsProperty String one, String two);

    public void jmsPropertyBooleanMethod(@JmsProperty Boolean one, String two);

    public void jmsPropertyPrimitiveBooleanMethod(@JmsProperty boolean one, String two);

    public void jmsPropertyByteMethod(@JmsProperty Byte one, String two);

    public void jmsPropertyPrimitiveByteMethod(@JmsProperty byte one, String two);

    public void jmsPropertyCharMethod(@JmsProperty Character one, String two);

    public void jmsPropertyPrimitiveCharMethod(@JmsProperty char one, String two);

    public void jmsPropertyShortMethod(@JmsProperty Short one, String two);

    public void jmsPropertyPrimitiveShortMethod(@JmsProperty short one, String two);

    public void jmsPropertyIntMethod(@JmsProperty Integer one, String two);

    public void jmsPropertyPrimitiveIntMethod(@JmsProperty int one, String two);

    public void jmsPropertyLongMethod(@JmsProperty Long one, String two);

    public void jmsPropertyPrimitiveLongMethod(@JmsProperty long one, String two);

    public void jmsPropertyFloatMethod(@JmsProperty Float one, String two);

    public void jmsPropertyPrimitiveFloatMethod(@JmsProperty float one, String two);

    public void jmsPropertyDoubleMethod(@JmsProperty Double one, String two);

    public void jmsPropertyPrimitiveDoubleMethod(@JmsProperty double one, String two);

    public void jmsPropertyTwiceMethod(@JmsProperty String one, @JmsProperty String two);

    public void jmsPropertyMethodWithHeaderOnly(@JmsProperty(headerOnly = true) String one,
            String two);

    public void jmsPropertyInNestedClass(@JmsProperty NestedAnnotated one, String two);

    public void jmsPropertyInCyclicClass(@JmsProperty(headerOnly = true) Cyclic one, String two);

    public void jmsPropertyOnCollectionType(@JmsProperty Collection<String> param);

    public void jmsPropertyOnArrayType(@JmsProperty String[] param);
}
