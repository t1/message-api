package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.*;

/**
 * Annotation processor that generates <code>jaxb.index</code> files. All packages that contain at
 * least one class that is annotated as {@link XmlRootElement} will get one index file containing a
 * list of the package-relative class names of all root elements.
 */
public class JaxbIndexGenerator extends AbstractGenerator {

    private final ListMultimap<String, TypeElement> packageMap = ArrayListMultimap.create();

    public JaxbIndexGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    /**
     * The packages mapped to all of the xml root elements contained
     */
    public void process(Set<? extends Element> elements) {
        for (Element element : elements) {
            // XmlRootElement can only annotated to type elements
            TypeElement typeElement = (TypeElement) element;
            String pkg = getPackageOf(typeElement);
            packageMap.put(pkg, typeElement);
        }
    }

    public void finish() {
        while (!packageMap.isEmpty()) {
            String key = packageMap.keySet().iterator().next();
            List<TypeElement> types = packageMap.removeAll(key);
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

    private void writeJaxbIndex(FileObject jaxbIndex, List<TypeElement> elements)
            throws IOException {
        List<String> compound = convert(elements);
        Collections.sort(compound);
        write(jaxbIndex, compound);
    }

    private List<String> convert(List<TypeElement> elements) {
        List<String> compound = Lists.newArrayList();
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
