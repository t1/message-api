package net.java.messageapi.processor.mock;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;

import com.google.common.base.Supplier;

public class FilerDummy implements Filer {

    private final Supplier<Writer> writeTo;

    public FilerDummy() {
        this(new Supplier<Writer>() {
            @Override
            public Writer get() {
                return NullWriter.NULL_WRITER;
            }
        });
    }

    public FilerDummy(Supplier<Writer> writeTo) {
        this.writeTo = writeTo;
    }

    @Override
    public JavaFileObject createClassFile(CharSequence name, Element... originatingElements)
            throws IOException {
        return new JavaFileObjectDummy(Kind.CLASS, writeTo.get());
    }

    @Override
    public FileObject createResource(Location location, CharSequence pkg,
            CharSequence relativeName, Element... originatingElements) throws IOException {
        return new FileObjectDummy(writeTo.get());
    }

    @Override
    public JavaFileObject createSourceFile(CharSequence name, Element... originatingElements)
            throws IOException {
        return new JavaFileObjectDummy(Kind.SOURCE, writeTo.get());
    }

    @Override
    public FileObject getResource(Location location, CharSequence pkg, CharSequence relativeName)
            throws IOException {
        return new FileObjectDummy(writeTo.get());
    }
}
