package net.java.messageapi.adapter.xml;

import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * You can use one of the three configuration mechanisms built into JAXB yourself, or you can use
 * this enum for convenience; the main purpose of this enum is to ease testing.
 * <p>
 * NOTE: {@link #createJaxbContextFor(Package)} restores the previous configuration. This is very
 * important to keep tests independent of each another.
 */
public enum JaxbProvider {

    /**
     * Don't change the provider... the configuration can remain as it is. This may be the
     * {@link #SUN_JDK default} or it may already have been configured externally.
     */
    UNCHANGED(null),

    /**
     * The reference implementation bundled with the Sun JDK.
     */
    SUN_JDK(null),

    /** EclipseLink MOXy: http://wiki.eclipse.org/EclipseLink/FAQ/WhatIsMOXy */
    ECLIPSE_LINK("org.eclipse.persistence.jaxb.JAXBContextFactory"),

    /** An adapter for http://xstream.codehaus.org/ */
    XSTREAM("net.java.messageapi.xstream.XStreamJaxbContextFactory");

    public static class JaxbProviderMemento {
        private final String oldFactoryName;

        public JaxbProviderMemento(JaxbProvider jaxbProvider) {
            this.oldFactoryName = System.getProperty(PROPERTY);
            setFactory(jaxbProvider.factoryName);
        }

        private static void setFactory(String name) {
            if (name == null) {
                System.clearProperty(PROPERTY);
            } else {
                System.setProperty(PROPERTY, name);
            }
        }

        public void restore() {
            setFactory(oldFactoryName);
        }
    }

    private static final String PROPERTY = javax.xml.bind.JAXBContext.class.getName();

    private final String factoryName;

    private JaxbProvider(String factoryName) {
        this.factoryName = factoryName;
    }

    /**
     * Creates a {@link JAXBContext} using this JAXB provider.
     * <p>
     * Note that we currently require only support for newInstance with a package-path and an array
     * of classes; maybe others have to be supported later.
     */
    public JAXBContext createJaxbContextFor(Package pkg) {
        JaxbProviderMemento memento = setUp();
        try {
            return JAXBContext.newInstance(pkg.getName());
        } catch (JAXBException e) {
            throw new RuntimeException("can't create JAXB context for " + pkg, e);
        } finally {
            memento.restore();
        }
    }

    /**
     * Creates a {@link JAXBContext} using this JAXB provider.
     * <p>
     * Note that we currently require only support for newInstance with a package-path and an array
     * of classes; maybe others have to be supported later.
     */
    public JAXBContext createJaxbContextFor(Class<?>... classesToBeBound) {
        JaxbProviderMemento memento = setUp();
        try {
            return JAXBContext.newInstance(classesToBeBound);
        } catch (JAXBException e) {
            throw new RuntimeException("can't create JAXB context for "
                    + Arrays.asList(classesToBeBound), e);
        } finally {
            memento.restore();
        }
    }

    /**
     * Activate this JaxbProvider
     * 
     * @return a memento to {@link JaxbProviderMemento#restore() restore} the previous state
     */
    public JaxbProviderMemento setUp() {
        return new JaxbProviderMemento(this);
    }
}