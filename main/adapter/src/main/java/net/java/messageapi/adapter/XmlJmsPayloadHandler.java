package net.java.messageapi.adapter;

import java.io.*;

import javax.jms.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

/**
 * A {@link JmsPayloadHandler} that serializes calls as XML text messages.
 */
@XmlRootElement
public class XmlJmsPayloadHandler extends JmsPayloadHandler {

    @XmlTransient
    private final JaxbProvider jaxbProvider;

    public XmlJmsPayloadHandler() {
        this(JaxbProvider.UNCHANGED);
    }

    public XmlJmsPayloadHandler(JaxbProvider jaxbProvider) {
        this.jaxbProvider = jaxbProvider;
    }

    @Override
    public String toPayload(Object pojo) {
        Writer writer = new StringWriter();
        convert(writer, pojo);
        return writer.toString();
    }

    public void convert(Writer writer, Object pojo) {
        Marshaller marshaller = createMarshaller(pojo);
        marshalPojo(marshaller, pojo, writer);
    }

    private <T> Marshaller createMarshaller(Object pojo) {
        Class<? extends Object> type = pojo.getClass();
        try {
            JAXBContext context = jaxbProvider.createJaxbContextFor(type);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException("can't create marshaller for " + type, e);
        }
    }

    private void marshalPojo(Marshaller marshaller, Object pojo, Writer writer) {
        try {
            marshaller.marshal(pojo, writer);
        } catch (JAXBException e) {
            throw new RuntimeException("can't marshal " + pojo, e);
        }
    }

    @Override
    public javax.jms.Message createJmsMessage(Object payload, Session session) throws JMSException {
        return session.createTextMessage((String) payload);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "xml";
    }

    @Override
    public String toString() {
        return super.toString() + " (JAXB=" + jaxbProvider + ")";
    }
}
