package net.java.messageapi.xstream;

import java.util.Map;

import javax.xml.bind.JAXBContext;

/**
 * This creates a very rudimentary JAXBContext for XStream: Only marshalling and
 * only a very limited set of JAXB annotations is supported.
 */
public class XStreamJaxbContextFactory {

	public static JAXBContext createContext(String contextPath,
			ClassLoader classLoader, Map<String, Object> properties) {
		return new XStreamJaxbContext(contextPath, classLoader, properties);
	}

	public static JAXBContext createContext(Class<?>[] classes,
			Map<String, Object> properties) {
		return new XStreamJaxbContext(classes, properties);
	}
}
