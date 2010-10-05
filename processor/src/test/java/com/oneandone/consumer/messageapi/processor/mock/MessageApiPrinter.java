package com.oneandone.consumer.messageapi.processor.mock;

import java.io.*;

import com.google.common.base.Supplier;
import com.oneandone.consumer.messageapi.processor.MessageApiAnnotationProcessor;

public class MessageApiPrinter {
    private static final Supplier<Writer> TO_SYSTEM_OUT = new Supplier<Writer>() {
        @Override
        public Writer get() {
            return new OutputStreamWriter(System.out) {
                @Override
                public void close() throws IOException {
                    super.flush();
                    // don't close System.out
                }
            };
        }
    };

    public static void main(String[] args) throws Exception {
        ProcessingEnvironmentDummy env = new ProcessingEnvironmentDummy(new PrintMessager(),
                new FilerDummy(TO_SYSTEM_OUT));

        MessageApiAnnotationProcessor processor = new MessageApiAnnotationProcessor();

        env.process(processor, Class.forName(args[0]));
    }
}
