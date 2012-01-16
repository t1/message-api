package net.java.messageapi.adapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionSupplier implements JmsHeaderSupplier {
    Logger log = LoggerFactory.getLogger(VersionSupplier.class);

    String getVersion(Class<?> api) {
        String version = api.getPackage().getSpecificationVersion();
        if (version == null)
            version = api.getPackage().getImplementationVersion();
        if (version == null)
            version = extractInterfaceVersion(api);
        return version;
    }

    private String extractInterfaceVersion(Class<?> api) {
        String className = api.getSimpleName() + ".class";
        URL resource = api.getResource(className);
        log.debug("get resource for {} -> {}", className, pathOf(api));
        if (resource == null)
            return null;
        String classPath = resource.toString();
        if (classPath.startsWith("jar")) {
            String jarPath = classPath.substring(0, classPath.lastIndexOf("!") + 1);
            Attributes attributes = getManifestAttributes(jarPath, api);
            if (attributes != null) {
                return attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
        } else if (classPath.endsWith(pathOf(api))) {
            String jarPath = classPath.substring(0, classPath.length() - pathOf(api).length());
            log.debug("############# {}", jarPath);
            Attributes attributes = getManifestAttributes(jarPath, api);
            if (attributes != null) {
                return attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
        } else if (classPath.contains("/WEB-INF/")) {
            String jarPath = classPath.substring(0, classPath.lastIndexOf("/WEB-INF/"));
            Attributes attributes = getManifestAttributes(jarPath, api);
            if (attributes != null) {
                return attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
        } else {
            log.error("Could not extract version for " + api + ": Invalid class path " + classPath + ".");
        }
        return null;
    }

    private String pathOf(Class<?> api) {
        return api.getCanonicalName().replace('.', '/');
    }

    private Attributes getManifestAttributes(String jarPath, Class<?> api) {
        String manifestPath = jarPath + "/META-INF/MANIFEST.MF";
        try {
            log.debug("manifest path: {}", manifestPath);
            Manifest manifest = new Manifest(new URL(manifestPath).openStream());
            return manifest.getMainAttributes();
        } catch (MalformedURLException e) {
            log.error("Could not extract version for " + api + ": Malformed manifest path URL " + manifestPath + ".", e);
            return null;
        } catch (IOException e) {
            log.error("Could not extract version for " + api + ": Unable to access MANIFEST.MF located at "
                    + manifestPath + ".", e);
            return null;
        }
    }

    @Override
    public void addTo(Message message, Object pojo) throws JMSException {
        String version = getVersion(pojo.getClass());
        if (version != null) {
            message.setStringProperty("VERSION", version);
        }
    }
}
