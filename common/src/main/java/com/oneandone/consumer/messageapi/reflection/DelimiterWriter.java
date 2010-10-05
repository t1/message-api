/**
 * 
 */
package com.oneandone.consumer.messageapi.reflection;

import java.io.IOException;
import java.io.Writer;

public class DelimiterWriter {

    private final Writer writer;
    private final String string;

    private boolean first = true;

    public DelimiterWriter(Writer writer, String string) {
        this.writer = writer;
        this.string = string;
    }

    public void write() {
        if (first) {
            first = false;
        } else {
            try {
                writer.write(string);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}