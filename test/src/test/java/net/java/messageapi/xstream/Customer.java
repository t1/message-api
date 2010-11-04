/**
 * 
 */
package net.java.messageapi.xstream;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {
    @XmlAttribute
    int age;
    Address billingAddress;
    Address shippingAddress;
}