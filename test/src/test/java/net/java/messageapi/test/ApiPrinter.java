package net.java.messageapi.test;

import java.io.OutputStreamWriter;
import java.io.Writer;

import net.java.messageapi.adapter.xml.ToXmlSenderFactory;
import net.java.messageapi.test.TestApi;
import net.java.messageapi.test.TestType;


public class ApiPrinter {

    public static void main(String[] args) throws Exception {
        final Writer writer = new OutputStreamWriter(System.out);
        TestApi api = ToXmlSenderFactory.create(TestApi.class, writer).get();

        api.namespaceCall(new TestType("hiho"));
    }
}
