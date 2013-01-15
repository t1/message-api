package net.java.messageapi.processor;

import java.io.*;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Annotation processor that generates <code>jaxb.index</code> files. All packages that contain at least one class that
 * is annotated as {@link XmlRootElement} will get one index file containing a list of the package-relative class names
 * of all root elements.
 */
public class JaxbIndexGenerator extends AbstractGenerator {

    private final Map<String, List<TypeElement>> packageMap = new HashMap<String, List<TypeElement>>();

    public JaxbIndexGenerator(Messager messager, Filer filer, Elements utils) {
        super(messager, filer, utils);
    }

    @Override
    public void process(Element element) {
        // XmlRootElement can only be annotated to type elements
        TypeElement typeElement = (TypeElement) element;
        String pkg = getPackageOf(typeElement);
        List<TypeElement> list = packageMap.get(pkg);
        if (list == null) {
            list = new ArrayList<TypeElement>();
            packageMap.put(pkg, list);
        }
        list.add(typeElement);
    }

    public void finish() {
        while (!packageMap.isEmpty()) {
            String key = packageMap.keySet().iterator().next();
            List<TypeElement> types = packageMap.remove(key);
            generate(key, types);
        }
    }

    private void generate(String pkg, List<TypeElement> types) {
        try {
            note("Writing jaxb.index for " + pkg + " with " + types.size() + " root elements");
            FileObject jaxbIndex = createResourceFile(pkg, "jaxb.index", types);
            writeJaxbIndex(jaxbIndex, types);
        } catch (IOException e) {
            error("can't write jaxb.index for " + pkg + ": " + e);
        }
    }

    private void writeJaxbIndex(FileObject jaxbIndex, List<TypeElement> elements) throws IOException {
        List<String> compound = convert(elements);
        Collections.sort(compound);
        write(jaxbIndex, compound);
    }

    private List<String> convert(List<TypeElement> elements) {
        List<String> compound = new ArrayList<String>();
        for (TypeElement element : elements) {
            compound.add(getCompoundName(element));
        }
        return compound;
    }

    private void write(FileObject jaxbIndex, List<String> compound) throws IOException {
        Writer writer = jaxbIndex.openWriter();
        try {
            for (String element : compound) {
                writer.append(element).append('\n');
            }
        } finally {
            writer.close();
        }
    }
}
