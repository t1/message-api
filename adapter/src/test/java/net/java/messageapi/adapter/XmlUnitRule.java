package net.java.messageapi.adapter;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/** {@link XMLUnit} is configured statically :-( This class fixes that. */
public class XmlUnitRule implements TestRule {
    private boolean ignoreWhitespace;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                boolean wasIgnoreWhitespace = XMLUnit.getIgnoreWhitespace();
                XMLUnit.setIgnoreWhitespace(ignoreWhitespace);
                try {
                    base.evaluate();
                } finally {
                    XMLUnit.setIgnoreWhitespace(wasIgnoreWhitespace);
                }
            }
        };
    }

    public XmlUnitRule ignoreWhitespace() {
        this.ignoreWhitespace = true;
        return this;
    }
}