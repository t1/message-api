package net.java.messageapi.test;

import java.io.OutputStreamWriter;
import java.io.Writer;

public class ApiPrinter {
    public static void main(String[] args) throws Exception {
        final Writer writer = new OutputStreamWriter(System.out);
        TestApi api = ToXmlEncoderHelper.create(TestApi.class, writer);

        api.namespaceCall(new TestType("hiho"));
    }
}
