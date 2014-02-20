package net.java.messageapi.tck.simple;

import javax.inject.Inject;

public class SimpleSender {
    @Inject
    private SimpleApi customerService;

    public void execute(String string) {
        customerService.simpleMethod(string);
    }
}
