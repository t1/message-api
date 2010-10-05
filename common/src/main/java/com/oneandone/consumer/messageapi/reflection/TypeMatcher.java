package com.oneandone.consumer.messageapi.reflection;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class TypeMatcher {
    private static final Set<String> PRIMITIVES = ImmutableSet.of("boolean", "byte", "char",
            "short", "int", "long", "float", "double");

    private static final Pattern TYPE_PATTERN = Pattern.compile("^([a-z.]*\\.)?([A-Za-z.]*)(<.*>)?(\\[\\])?$");

    /** package incl. the final dot */
    private final String pkg;
    /** simple name */
    private final String rawType;
    /** generic type */
    private final String generic;
    /** array */
    private final String array;

    public TypeMatcher(String type) {
        Matcher matcher = TYPE_PATTERN.matcher(type);
        if (!matcher.matches())
            throw new IllegalArgumentException("unparseable type [" + type + "]");

        this.pkg = matcher.group(1);
        this.rawType = matcher.group(2);
        this.generic = matcher.group(3);
        this.array = matcher.group(4);
    }

    public boolean requiresImportFor(String containerPackage) {
        if (PRIMITIVES.contains(rawType))
            return false;
        if ("java.lang.".equals(pkg))
            return false;
        if (pkg == null)
            fail("only primitive types have no package, not " + rawType);
        if (containerPackage.equals(getPackage()))
            return false;
        return true;
    }

    private String getPackage() {
        return pkg.substring(0, pkg.length() - 1); // the trailing '.'
    }

    /**
     * The list of all package names and the raw types that are required to make this type
     * {@link #getLocalType() local}.
     */
    public List<String> getImportTypesFor(String containerPackage) {
        ImmutableList.Builder<String> result = ImmutableList.builder();
        if (requiresImportFor(containerPackage))
            result.add(pkg + rawType);
        if (generic != null) {
            for (TypeMatcher subType : getGenericTypeMatchers()) {
                result.addAll(subType.getImportTypesFor(containerPackage));
            }
        }
        return result.build();
    }

    private List<TypeMatcher> getGenericTypeMatchers() {
        ImmutableList.Builder<TypeMatcher> result = ImmutableList.builder();

        for (String genericParameter : parseGeneric()) {
            result.add(new TypeMatcher(genericParameter));
        }

        return result.build();
    }

    /** Split that generic parameter list into separate type names. */
    private List<String> parseGeneric() {
        char[] gen = generic.substring(1).toCharArray(); // remove initial '<'
        gen[gen.length - 1] = ','; // replace final '>' with comma sentinel

        ImmutableList.Builder<String> matches = ImmutableList.builder();
        StringBuilder type = new StringBuilder();
        int level = 0;
        for (char c : gen) {
            switch (c) {
            case '<':
                type.append(c);
                level++;
                break;
            case '>':
                if (level == 0)
                    fail("unbalanced '>'");
                level--;
                type.append(c);
                break;
            case ' ':
            case '\t':
                if (level > 0)
                    type.append(c);
                break;
            case ',': // maybe sentinel
                if (level > 0)
                    type.append(c);
                if (level == 0) {
                    matches.add(type.toString());
                    type.setLength(0);
                }
                break;
            default:
                type.append(c);
            }
        }
        return matches.build();
    }

    private void fail(String message) {
        throw new RuntimeException(message);
    }

    /** Just the type name itself */
    public String getRawType() {
        return rawType;
    }

    /** The full name of the type but without the package */
    public String getLocalType() {
        StringWriter out = new StringWriter();
        out.append(rawType);
        if (generic != null) {
            out.append('<');
            DelimiterWriter comma = new DelimiterWriter(out, ", ");
            for (TypeMatcher subType : getGenericTypeMatchers()) {
                comma.write();
                out.append(subType.getLocalType());
            }
            out.append('>');
        }
        if (array != null)
            out.append(array);
        return out.toString();
    }

    @Override
    public String toString() {
        return "TypeMatcher [" //
                + ((pkg == null) ? "" : pkg) //
                + rawType + ((generic == null) ? "" : generic) //
                + ((array == null) ? "" : array) //
                + "]";
    }
}