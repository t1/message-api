package com.oneandone.consumer.messageapi.xstream;

import static com.oneandone.consumer.messageapi.test.RegexMatcher.*;
import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.bind.*;

import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.consumer.messageapi.adapter.xml.JaxbProvider;

@RunWith(TwiP.class)
public class XStreamTest {

    private static final Package PACKAGE = XStreamTest.class.getPackage();

    @Test
    public void shouldLoad(JaxbProvider jaxbProvider) throws Exception {
        // Given
        Customer customer = createCustomer();

        // When
        StringWriter writer = marshall(jaxbProvider, customer);

        // Then
        String[] line = writer.toString().split("\n");
        int i = 0;
        assertThat(line[i++],
                matches("<\\?xml version=\"1.0\" encoding=\"UTF-8\"( standalone=\"yes\")?\\?>"));
        assertThat(line[i++], matches("<customer age=\"34\">"));
        assertThat(line[i++], matches("\\s*<billingAddress id=\"1\">"));
        assertThat(line[i++], matches("\\s*<street>1 Billing Street</street>"));
        assertThat(line[i++], matches("\\s*</billingAddress>"));
        assertThat(line[i++], matches("\\s*<shippingAddress id=\"2\">"));
        assertThat(line[i++], matches("\\s*<street>2 Shipping Road</street>"));
        assertThat(line[i++], matches("\\s*</shippingAddress>"));
        assertThat(line[i++], matches("</customer>"));
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.age = 34;

        Address billingAddress = new Address();
        billingAddress.id = 1;
        billingAddress.street = "1 Billing Street";
        customer.billingAddress = billingAddress;

        Address shippingAddress = new Address();
        shippingAddress.id = 2;
        shippingAddress.street = "2 Shipping Road";
        customer.shippingAddress = shippingAddress;
        return customer;
    }

    private StringWriter marshall(JaxbProvider jaxbProvider, Customer customer)
            throws JAXBException, PropertyException {
        JAXBContext context = jaxbProvider.createJaxbContextFor(PACKAGE);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(customer, writer);
        return writer;
    }
}
