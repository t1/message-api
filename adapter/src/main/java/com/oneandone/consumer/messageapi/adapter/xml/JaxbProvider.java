package com.oneandone.consumer.messageapi.adapter.xml;

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
     * {@link #DEFAULT} or it may already have been configured externally.
     */
    UNCHANGED(null),

    /**
     * The reference implementation bundled with the Sun JDK.
     */
    SUN_JDK(null),

    /** EclipseLink MOXy: http://wiki.eclipse.org/EclipseLink/FAQ/WhatIsMOXy */
    ECLIPSE_LINK("org.eclipse.persistence.jaxb.JAXBContextFactory"),

    /** An adapter for http://xstream.codehaus.org/ */
    XSTREAM("com.oneandone.consumer.messageapi.xstream.XStreamJaxbContextFactory");

    private static final String PROPERTY = javax.xml.bind.JAXBContext.class.getName();

    private static void setFactory(String name) {
        if (name == null) {
            System.clearProperty(PROPERTY);
        } else {
            System.setProperty(PROPERTY, name);
        }
    }

    private final String factoryName;

    private JaxbProvider(String factoryName) {
        this.factoryName = factoryName;
    }

    /**
     * Creates a {@link JAXBContext} using this JAXB provider.
     * <p>
     * Note that we currently require only support for {@link JAXBContext#newInstance(String)
     * newInstance with a package-path}; maybe others have to be supported later.
     */
    public JAXBContext createJaxbContextFor(Package pkg) {
        final String oldFactoryName = System.getProperty(PROPERTY);
        try {
            setFactory(factoryName);
            return JAXBContext.newInstance(pkg.getName());
        } catch (JAXBException e) {
            throw new RuntimeException("can't create JAXB context for " + pkg, e);
        } finally {
            setFactory(oldFactoryName);
        }
    }
}