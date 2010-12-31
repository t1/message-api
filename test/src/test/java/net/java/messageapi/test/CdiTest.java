package net.java.messageapi.test;

import javax.jms.JMSException;

import net.sf.twip.TwiP;
import net.sf.twip.TwipExtensions;
import net.sf.twip.cdi.CdiExtension;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
@TwipExtensions(CdiExtension.class)
@Ignore("weld wants to load UIComponent, but we only have the class stub from javaee=api")
public class CdiTest {

    @Test
    public void messageRoundtrip() throws JMSException {
        System.out.println("hi");
    }
}
