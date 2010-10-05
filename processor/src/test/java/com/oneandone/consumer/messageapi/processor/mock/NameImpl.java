package com.oneandone.consumer.messageapi.processor.mock;

import javax.lang.model.element.Name;

class NameImpl implements Name {

    private final CharSequence content;

    public NameImpl(CharSequence content) {
        if (content == null)
            throw new NullPointerException("name contents must not be null");
        this.content = content;
    }

    @Override
    public boolean contentEquals(CharSequence that) {
        return content.equals(that);
    }

    @Override
    public char charAt(int index) {
        return content.charAt(index);
    }

    @Override
    public int length() {
        return content.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return content.subSequence(start, end);
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
