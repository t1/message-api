package com.oneandone.consumer.messageapi.processor.mock;

import java.io.Writer;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

class JavaFileObjectDummy extends FileObjectDummy implements JavaFileObject {

    private final Kind kind;

    public JavaFileObjectDummy(Kind kind, Writer writer) {
        super(writer);
        this.kind = kind;
    }

    @Override
    public Modifier getAccessLevel() {
        return Modifier.PUBLIC;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        throw new UnsupportedOperationException();
    }
}
