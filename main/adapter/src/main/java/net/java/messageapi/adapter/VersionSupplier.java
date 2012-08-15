package net.java.messageapi.adapter;

import java.io.FileNotFoundException;
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

    public String getVersion(Class<?> api) {
        log.trace("getVersion for {} -> {}/{}", new Object[] { api, api.getPackage().getImplementationVersion(),
                api.getPackage().getSpecificationVersion() });
        String version = api.getPackage().getImplementationVersion();
        if (version == null)
            version = api.getPackage().getSpecificationVersion();
        if (version == null)
            version = readVersionFromManifest(api);
        return version;
    }

    private String readVersionFromManifest(Class<?> api) {
        String className = api.getSimpleName() + ".class";
        URL resource = api.getResource(className);
        log.trace("get resource for {} -> {} -> {}", new Object[] { className, pathOf(api), resource });
        if (resource == null)
            return null;
        String classPath = resource.toString();
        String version = null;
        if (classPath.startsWith("jar:"))
            version = version(classPath.substring(0, classPath.lastIndexOf("!") + 1));
        if (version == null && classPath.endsWith(pathOf(api) + ".class"))
            version = version(classPath.substring(0, classPath.length() - pathOf(api).length() - 7));
        if (version == null && classPath.contains("/WEB-INF/"))
            version = version(classPath.substring(0, classPath.lastIndexOf("/WEB-INF/")));
        if (version == null)
            log.error("Could not extract version for {}: Invalid class path {}.", api, classPath);
        return version;
    }

    private String version(String jarPath) {
        Attributes attributes = getManifestAttributes(jarPath);
        if (attributes == null) {
            log.debug("no main attributes in manifest {}", jarPath);
            return null;
        }
        String version = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
        log.trace("found specification version {}", version);
        return version;
    }

    private String pathOf(Class<?> api) {
        return api.getCanonicalName().replace('.', '/');
    }

    private Attributes getManifestAttributes(String jarPath) {
        String manifestPath = jarPath + "/META-INF/MANIFEST.MF";
        try {
            log.trace("manifest path: {}", manifestPath);
            Manifest manifest = new Manifest(new URL(manifestPath).openStream());
            log.trace("found entries: {}", manifest.getEntries().keySet());
            return manifest.getMainAttributes();
        } catch (MalformedURLException e) {
            log.error("Could not extract version", e);
            return null;
        } catch (FileNotFoundException e) {
            log.debug("No manifest found at {}", manifestPath);
            return null;
        } catch (IOException e) {
            log.error("Could not extract version", e);
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
