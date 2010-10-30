package com.oneandone.consumer.messageapi.processor.mock;

import java.io.IOException;
import java.io.Writer;

public class NullWriter extends Writer {

    public static final Writer NULL_WRITER = new NullWriter();

    @Override
    public void close() throws IOException {
        // do nothing
    }

    @Override
    public void flush() throws IOException {
        // do nothing
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        // do nothing
    }
}
