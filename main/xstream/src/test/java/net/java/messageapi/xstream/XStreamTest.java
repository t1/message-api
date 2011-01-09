package net.java.messageapi.xstream;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import javax.xml.bind.*;

import org.junit.*;

public class XStreamTest {

	private static final String PACKAGE = XStreamTest.class.getPackage().getName();
	private static final String JAXB_PROPERTY = javax.xml.bind.JAXBContext.class.getName();

	@Before
	public void before() {
		System.setProperty(JAXB_PROPERTY, XStreamJaxbContextFactory.class.getName());
	}

	@After
	public void after() {
		System.clearProperty(JAXB_PROPERTY);
	}

	@Test
	public void shouldLoad() throws Exception {
		// Given
		Customer customer = createCustomer();

		// When
		String writer = marshall(customer);

		// Then
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
				+ "<customer age=\"34\">\n" //
				+ "  <billingAddress id=\"1\">\n" //
				+ "    <street>1 Billing Street</street>\n" //
				+ "  </billingAddress>\n" //
				+ "  <shippingAddress id=\"2\">\n" //
				+ "    <street>2 Shipping Road</street>\n" //
				+ "  </shippingAddress>\n" //
				+ "</customer>", writer);
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

	private String marshall(Customer customer) throws JAXBException, PropertyException {
		JAXBContext context = JAXBContext.newInstance(PACKAGE);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter writer = new StringWriter();
		marshaller.marshal(customer, writer);
		return writer.toString();
	}
}
