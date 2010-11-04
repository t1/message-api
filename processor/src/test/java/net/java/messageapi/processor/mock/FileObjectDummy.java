/**
 * 
 */
package net.java.messageapi.processor.mock;

import java.io.*;
import java.net.URI;

import javax.tools.FileObject;

class FileObjectDummy implements FileObject {

    private final Writer writer;

    public FileObjectDummy(Writer writer) {
        this.writer = writer;
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() throws IOException {
        return writer;
    }

    @Override
    public URI toUri() {
        throw new UnsupportedOperationException();
    }
}