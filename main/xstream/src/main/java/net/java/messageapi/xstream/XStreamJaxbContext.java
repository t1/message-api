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

	private XStreamJaxbContext(Map<String, Object> properties) {
		this.properties = properties;
		this.xStream = new XStream();
		xStream.autodetectAnnotations(true);
	}

	public XStreamJaxbContext(String contextPath, ClassLoader classLoader,
			Map<String, Object> properties) {
		this(properties);
		new XStreamAnnotationScanner(contextPath, classLoader, xStream);
	}

	public XStreamJaxbContext(Class<?>[] classes, Map<String, Object> properties) {
		this(properties);
		new XStreamAnnotationScanner(classes, xStream);
	}

	@Override
	public Marshaller createMarshaller() throws JAXBException {
		return new XStreamJaxbMarshaller(xStream);
	}

	@Override
	public Unmarshaller createUnmarshaller() throws JAXBException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Validator createValidator() throws JAXBException {
		throw new UnsupportedOperationException("it's deprecated anyway");
	}
}
