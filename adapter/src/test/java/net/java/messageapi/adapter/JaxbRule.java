package net.java.messageapi.adapter;

import javax.xml.bind.*;

import net.java.messageapi.adapter.JaxbProvider.JaxbProviderMemento;
import net.java.messageapi.adapter.JmsMappingAdapterTest.Container;
import net.java.messageapi.adapter.JmsMappingAdapterTest.SimpleType;
import net.java.messageapi.adapter.JmsMappingAdapterTest.SimpleTypeConverter;
import net.java.messageapi.converter.Converter;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class JaxbRule implements TestRule {
    private JAXBContext context;

    private JaxbProviderMemento memento;
    private JaxbProvider jaxbProvider;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                memento = jaxbProvider.setUp();
                context =
                        JAXBContext.newInstance(Container.class, Converter.class, SimpleTypeConverter.class,
                                SimpleType.class);
                try {
                    base.evaluate();
                } finally {
                    memento.restore();
                }
            }
        };
    }

    public void setProvider(JaxbProvider jaxbProvider) {
        this.jaxbProvider = jaxbProvider;
    }

    public Marshaller createMarshaller() throws JAXBException {
        return context.createMarshaller();
    }

    public Unmarshaller createUnmarshaller() throws JAXBException {
        return context.createUnmarshaller();
    }
}