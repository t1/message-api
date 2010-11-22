package net.java.messageapi.test;

import java.io.OutputStreamWriter;
import java.io.Writer;

import net.java.messageapi.adapter.xml.ToXmlEncoder;

public class ApiPrinter {

    public static void main(String[] args) throws Exception {
        final Writer writer = new OutputStreamWriter(System.out);
        TestApi api = ToXmlEncoder.create(TestApi.class, writer);

        api.namespaceCall(new TestType("hiho"));
    }
}
