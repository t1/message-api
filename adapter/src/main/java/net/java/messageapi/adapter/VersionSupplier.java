package net.java.messageapi.adapter;

import java.io.*;
import java.net.URL;
import java.util.jar.*;

import javax.jms.*;

import org.slf4j.*;

public class VersionSupplier implements JmsHeaderSupplier {
    private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
    Logger log = LoggerFactory.getLogger(VersionSupplier.class);

    @Override
    public void addTo(Message message, Class<?> api, Object pojo) throws JMSException {
        log.trace("applyTo api={}, pojo={}", api, pojo);
        String version = getVersion(api);
        if (version != null) {
            message.setStringProperty("VERSION", version);
        }
    }

    public String getVersion(Class<?> api) {
        log.trace("getVersion for {}", api);
        Package pkg = api.getPackage();
        log.trace("    -> {}/{}", pkg.getImplementationVersion(), pkg.getSpecificationVersion());
        String version = pkg.getSpecificationVersion();
        if (version == null)
            version = pkg.getImplementationVersion();
        if (version == null) {
            try {
                version = readVersionFromManifest(api);
            } catch (IOException e) {
                log.error("Could not extract version for " + api, e);
                return null;
            }
        }
        return version;
    }

    private String readVersionFromManifest(Class<?> api) throws IOException {
        String className = api.getSimpleName() + ".class";
        URL resource = api.getResource(className);
        log.trace("get resource for {} -> {} -> {}", new Object[] { className, pathOf(api), resource });
        if (resource == null)
            return null;
        String version = null;
        String classPath = resource.toString();
        if (classPath.startsWith("jar:"))
            version = version(classPath.substring(0, classPath.lastIndexOf("!") + 1));
        if (version == null && classPath.endsWith(pathOf(api) + ".class"))
            version = version(classPath.substring(0, classPath.length() - pathOf(api).length() - 7));
        if (version == null && classPath.contains("/WEB-INF/"))
            version = version(classPath.substring(0, classPath.lastIndexOf("/WEB-INF/")));
        log.debug("version {} for {} from {}.", version, api, classPath);
        return version;
    }

    private String pathOf(Class<?> api) {
        return api.getCanonicalName().replace('.', '/');
    }

    private String version(String jarPath) throws IOException {
        Attributes attributes = getManifestAttributes(jarPath);
        if (attributes == null) {
            log.trace("no main attributes in manifest {}", jarPath);
            return null;
        }
        String version = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
        log.trace("found specification version {}", version);
        return version;
    }

    private Attributes getManifestAttributes(String jarPath) throws IOException {
        String manifestPath = jarPath + MANIFEST_PATH;
        try {
            log.trace("manifest path: {}", manifestPath);
            Manifest manifest = new Manifest(new URL(manifestPath).openStream());
            log.trace("found entries: {}", manifest.getEntries().keySet());
            return manifest.getMainAttributes();
        } catch (FileNotFoundException e) {
            log.trace("No manifest found at {}", manifestPath);
            return null;
        }
    }
}
