/**
 * 
 */
package net.java.messageapi.xstream;

import java.util.Map;

import javax.xml.bind.*;

import com.thoughtworks.xstream.XStream;

/**
 * {@link JAXBContext} for XStream.
 */
class XStreamJaxbContext extends JAXBContext {

    @SuppressWarnings("unused")
    private final Map<String, Object> properties;
    private final XStream xStream;

    public XStreamJaxbContext(String contextPath, ClassLoader classLoader,
            Map<String, Object> properties) {
        this.properties = properties;
        this.xStream = new XStream();
        xStream.autodetectAnnotations(true);

        new XStreamAnnotationScanner(contextPath, classLoader, xStream).run();
    }

    @Override
    public Marshaller createMarshaller() throws JAXBException {
        return new XStreamJaxbMarshaller(xStream);
    }

    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        // TODO create Unmarshaller
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Validator createValidator() throws JAXBException {
        throw new UnsupportedOperationException("it's deprecated anyway");
    }
}
