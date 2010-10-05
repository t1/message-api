package com.oneandone.consumer.messageapi.test;

import java.io.OutputStreamWriter;
import java.io.Writer;

import com.oneandone.consumer.messageapi.adapter.xml.ToXmlSenderFactory;

public class ApiPrinter {

    public static void main(String[] args) throws Exception {
        final Writer writer = new OutputStreamWriter(System.out);
        TestApi api = ToXmlSenderFactory.create(TestApi.class, writer).get();

        api.namespaceCall(new TestType("hiho"));
    }
}
