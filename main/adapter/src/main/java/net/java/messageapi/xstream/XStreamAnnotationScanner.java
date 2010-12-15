package net.java.messageapi.xstream;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

import com.thoughtworks.xstream.XStream;

/**
 * Scans the JAXB annotations of the classes in the given contextPath (colon separated path of
 * package names). The acquired info is then added to XStream.
 */
class XStreamAnnotationScanner implements Runnable {
    private final String contextPath;
    private final ClassLoader classLoader;
    private final XStream xStream;

    private final Set<Class<?>> visited = new HashSet<Class<?>>();

    public XStreamAnnotationScanner(String contextPath, ClassLoader classLoader, XStream xStream) {
        this.contextPath = contextPath;
        this.classLoader = classLoader;
        this.xStream = xStream;
    }

    @Override
    public void run() {
        try {
            scanPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanPath() throws IOException, ClassNotFoundException {
        for (String pathElement : contextPath.split(":")) {
            scanDirectory(pathElement);
        }
    }

    private void scanDirectory(String pathElement) throws IOException, ClassNotFoundException {
        String jaxbIndexFileName = pathElement.replace('.', File.separatorChar) + "/jaxb.index";
        URL jaxbIndexUrl = classLoader.getResource(jaxbIndexFileName);
        if (jaxbIndexUrl != null) {
            scanIndexFile(jaxbIndexUrl, pathElement);
        }
    }

    private void scanIndexFile(URL jaxbIndex, String prefix) throws IOException,
            ClassNotFoundException {
        LineNumberReader reader = new LineNumberReader(
                new InputStreamReader(jaxbIndex.openStream()));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String fullClassName = prefix + "." + line;
            Class<?> type = Class.forName(fullClassName, true, classLoader);
            scanType(type);
        }
    }

    private void scanType(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(XmlAttribute.class)) {
                XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
                xStream.useAttributeFor(type, field.getName());
                if (!"##default".equals(attribute.name())) {
                    xStream.aliasField(field.getName(), type, attribute.name());
                }
            }
            Class<?> subType = field.getType();
            if (mustBeVisited(subType, visited)) {
                visited.add(subType);
                scanType(subType);
            }
        }
    }

    private boolean mustBeVisited(Class<?> type, Set<Class<?>> visited) {
        return !type.isPrimitive() && !visited.contains(type);
    }
}