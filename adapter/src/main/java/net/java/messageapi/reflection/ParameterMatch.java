package net.java.messageapi.reflection;

import java.util.*;

/**
 * @see #parse(String)
 */
public class ParameterMatch {

    /**
     * Parse that parameter list string for the parameters with their types and names.
     */
    public static List<ParameterMatch> parse(String parameters) {
        List<ParameterMatch> matches = new ArrayList<ParameterMatch>();
        StringBuilder type = new StringBuilder();
        StringBuilder name = new StringBuilder();
        int level = 0;
        boolean typeDone = false;
        // append a comma as a sentinel
        for (char c : (parameters + ",").toCharArray()) {
            switch (c) {
            case '<':
                if (typeDone)
                    fail("'<' in variable name");
                level++;
                break;
            case '>':
                if (level == 0)
                    fail("unbalanced '>'");
                if (typeDone)
                    fail("'>' in variable name");
                level--;
                break;
            case ' ':
            case '\t':
                if (level == 0 && type.length() > 0) {
                    typeDone = true;
                }
                break;
            case ',': // maybe sentinel
                if (level == 0) {
                    matches.add(new ParameterMatch(type.toString(), name.toString()));
                    type.setLength(0);
                    name.setLength(0);
                    typeDone = false;
                }
                break;
            default:
                if (level == 0) {
                    if (typeDone) {
                        name.append(c);
                    } else {
                        type.append(c);
                    }
                }
            }
        }
        return matches;
    }

    private static void fail(String message) {
        throw new RuntimeException(message);
    }

    private final String typeName;
    private final String parameterName;

    public ParameterMatch(String typeName, String parameterName) {
        this.typeName = typeName;
        this.parameterName = parameterName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public boolean matches(Class<?> type) {
        return type.getCanonicalName().equals(typeName);
    }

    @Override
    public String toString() {
        return "ParameterMatch [" + typeName + " " + parameterName + "]";
    }
}
