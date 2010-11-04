package net.java.messageapi.test;

import java.util.regex.Pattern;

import org.hamcrest.*;

public class RegexMatcher extends BaseMatcher<String> {

    public static Matcher<String> matches(final String regex) {
        return new RegexMatcher(regex);
    }

    private final Pattern pattern;

    public RegexMatcher(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean matches(Object string) {
        return pattern.matcher((String) string).matches();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("regex: ").appendText(pattern.pattern());
    }
}