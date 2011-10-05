package net.java.messageapi.reflection;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

public class ParameterMapParser {

    public static final String SUFFIX = ".parametermap";

    private static final Pattern METHOD_SIGNATURE = Pattern.compile("(\\w*)\\((.*)\\)");

    private final Method method;
    private final ImmutableList<String> parameterNames;

    public ParameterMapParser(Method method) {
        this.method = method;
        this.parameterNames = fetchParameterNames();
    }

    private ImmutableList<String> fetchParameterNames() {
        List<String> content = getParameterMap();
        if (content == null) {
            ImmutableList.Builder<String> result = ImmutableList.builder();
            final int n = method.getParameterTypes().length;
            for (int i = 0; i < n; i++) {
                result.add("arg" + i);
            }
            return result.build();
        }
        for (String line : content) {
            if (line.startsWith("#"))
                continue;
            ImmutableList<String> matchedNames = matchedNames(line);
            if (matchedNames == null)
                continue;
            return matchedNames;
        }
        throw new InvalidParameterMapFileException(method, buildErrorDetails(content));
    }

    private List<String> getParameterMap() {
        Class<?> container = method.getDeclaringClass();
        // TODO use FQN and strip the package instead so nested classes work!
        String mapName = container.getSimpleName() + SUFFIX;
        URL url = container.getResource(mapName);
        if (url == null)
            return null;
        return getContent(url);
    }

    private List<String> getContent(URL url) {
        InputStream stream = null;
        try {
            stream = url.openStream();
            return readContent(stream);
        } catch (IOException e) {
            throw new RuntimeException("opening " + url, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore while closing
                }
            }
        }
    }

    private List<String> readContent(InputStream stream) throws IOException {
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(stream,
                Charset.defaultCharset()));
        ImmutableList.Builder<String> result = ImmutableList.builder();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            result.add(line);
        }
        return result.build();
    }

    private ImmutableList<String> matchedNames(String line) {
        Matcher methodMatcher = METHOD_SIGNATURE.matcher(line);
        if (!methodMatcher.matches())
            throw new InvalidParameterMapFileException(method, "[" + line
                    + "] doesn't seem to be a valid method signature.");
        if (!method.getName().equals(methodMatcher.group(1)))
            return null;

        ImmutableList.Builder<String> names = ImmutableList.builder();
        List<ParameterMatch> matches = ParameterMatch.parse(methodMatcher.group(2));
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> type = method.getParameterTypes()[i];
            ParameterMatch match = matches.get(i);
            if (!match.matches(type))
                return null;
            names.add(match.getParameterName());
        }

        return names.build();
    }

    private String buildErrorDetails(List<String> content) {
        StringBuilder message = new StringBuilder("method not found in:");
        for (String line : content) {
            if (line.startsWith("#"))
                continue;
            message.append("\n\t");
            message.append(line).append(" ==> ").append(appendLineDetails(line));
        }
        return message.toString();
    }

    /**
     * What's worse: duplicating the loop code or having one method do two things?
     */
    private String appendLineDetails(String line) {
        Matcher methodMatcher = METHOD_SIGNATURE.matcher(line);
        if (!methodMatcher.matches())
            throw new AssertionError("can't happen");
        if (!method.getName().equals(methodMatcher.group(1))) {
            return "name doesn't match";
        }

        List<ParameterMatch> matches = ParameterMatch.parse(methodMatcher.group(2));
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> type = method.getParameterTypes()[i];
            ParameterMatch match = matches.get(i);
            if (!match.matches(type)) {
                return "type " + type.getCanonicalName() + " doesn't match " + match;
            }
        }
        throw new AssertionError("can't happen");
    }

    public String getParameterName(int index) {
        return parameterNames.get(index);
    }
}
