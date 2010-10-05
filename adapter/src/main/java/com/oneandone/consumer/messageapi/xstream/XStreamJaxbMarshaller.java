package com.oneandone.consumer.messageapi.xstream;

import java.io.*;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBException;
import javax.xml.bind.helpers.AbstractMarshallerImpl;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.thoughtworks.xstream.XStream;

class XStreamJaxbMarshaller extends AbstractMarshallerImpl {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private final XStream xStream;

    public XStreamJaxbMarshaller(XStream xStream) {
        this.xStream = xStream;
    }

    @Override
    public void marshal(Object jaxbElement, Result result) throws JAXBException {
        Class<?> rootClass = jaxbElement.getClass();
        xStream.alias(getAlias(rootClass), rootClass);

        Writer writer = getWriter(result);
        writeHeader(writer);
        xStream.toXML(jaxbElement, writer);
    }

    private String getAlias(Class<? extends Object> rootClass) {
        String name = rootClass.getSimpleName();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private Writer getWriter(Result result) {
        StreamResult streamResult = (StreamResult) result;
        Writer writer = streamResult.getWriter();
        if (writer == null)
            writer = new OutputStreamWriter(streamResult.getOutputStream(), UTF_8);
        return writer;
    }

    private void writeHeader(Writer writer) {
        try {
            writer.append(XML_HEADER);
        } catch (IOException e) {
            throw new RuntimeException("can't write xml header", e);
        }
    }
}
