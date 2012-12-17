package net.java.messageapi.test;

import java.io.InputStream;
import java.util.Scanner;

public class VersionHelper {
    public static final String API_VERSION;
    static {
        InputStream stream = JmsXmlRoundtripTest.class.getResourceAsStream("/project.version");
        API_VERSION = new Scanner(stream).nextLine();
    }
}
