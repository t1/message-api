package net.java.messageapi.processor;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * Generates a java source file from a template... all variables of the form <code>${var}</code> are passed into the
 * {@link #replaceVariable(String)} method.
 * 
 * TODO change other generators to this, when possible
 */
public abstract class TemplateGenerator extends AbstractGenerator {
    protected static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Pattern VAR = Pattern.compile("\\$\\{([^}]+)\\}");

    protected TypeElement type;
    private final String templateName;

    public TemplateGenerator(Messager messager, ProcessingEnvironment env) {
        this(messager, env, null);
    }

    public TemplateGenerator(Messager messager, ProcessingEnvironment env, String templateName) {
        super(messager, env);
        this.templateName = templateName;
    }

    @Override
    synchronized public void process(Element element) {
        note("process " + path(element));
        this.type = (TypeElement) element;
        String targetTypeName = getTargetTypeName();
        note("Generating " + targetTypeName);

        String source = generateSource();
        try {
            JavaFileObject sourceFile = createSourceFile(targetTypeName, type);
            Writer writer = null;
            try {
                writer = sourceFile.openWriter();
                writer.write(source);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } catch (IOException e) {
            error("Can't write web resource\n" + e, type);
        } finally {
            this.type = null;
        }
    }

    private String path(Element element) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Element e = element; e != null; e = e.getEnclosingElement()) {
            if (first) {
                first = false;
            } else {
                result.append('/');
            }
            result.append(e.getKind());
            result.append(':');
            result.append(e.toString());
        }
        return result.toString();
    }

    private String generateSource() {
        StringBuffer result = new StringBuffer();
        BufferedReader reader = null;
        try {
            try {
                reader = new BufferedReader(getTemplateReader());
                String line;
                while ((line = reader.readLine()) != null) {
                    appendLine(result, line);
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }

    /**
     * The {@link Reader} for your template. Instead of overriding this method, you can also pass the name of a template
     * resource relative to your subclass into the {@link #TemplateGenerator(Messager, Filer, String)} constructor.
     */
    protected Reader getTemplateReader() {
        InputStream intputStream = this.getClass().getResourceAsStream(templateName);
        if (intputStream == null)
            throw new RuntimeException("can't find " + templateName);
        return new InputStreamReader(intputStream, UTF_8);
    }

    /** The fully qualified name of the Java type to be created. */
    abstract protected String getTargetTypeName();

    private void appendLine(StringBuffer result, String line) {
        Matcher matcher = VAR.matcher(line);
        while (matcher.find()) {
            matcher.appendReplacement(result, replaceVariable(matcher.group(1)));
        }
        matcher.appendTail(result).append('\n');
    }

    /** @see TemplateGenerator */
    abstract protected String replaceVariable(String variable);
}
