package net.java.messageapi.processor;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

public class MdbGenerator extends AbstractGenerator {
    public MdbGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    public void process(Element element) {
        TypeElement api = (TypeElement) element;
        String fileName = api.getQualifiedName() + "MDB";
        note("Generating " + fileName);

        String mdbSource = generate(api);
        Writer writer = null;
        try {
            JavaFileObject sourceFile = createSourceFile(fileName, api);
            writer = sourceFile.openWriter();
            writer.write(mdbSource);
        } catch (IOException e) {
            error("Can't write MDB\n" + e, api);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private String generate(TypeElement api) {
        String fqcn = api.getQualifiedName().toString();
        String simple = api.getSimpleName().toString();
        String pkg = getPackageOf(api);
        String destination = fqcn;
        String mdbName = simple + "MDB";

        // TODO add messageSelector on the version!

        return ""
                + ("package " + pkg + ";\n")
                + "\n" //
                + "import javax.ejb.ActivationConfigProperty;\n"
                + "import javax.ejb.MessageDriven;\n" //
                + "import javax.inject.Inject;\n" //
                + "import javax.jms.MessageListener;\n" //
                + "\n" //
                + ("import " + fqcn + ";\n")
                + "import net.java.messageapi.JmsIncoming;\n"
                + "import net.java.messageapi.adapter.MessageDecoder;\n"
                + "\n"
                + "@MessageDriven(messageListenerInterface = MessageListener.class, //\n"
                + "activationConfig = { @ActivationConfigProperty(\n"
                + ("propertyName = \"destination\", propertyValue = \"" + destination + "\") })\n")
                + ("public class " + mdbName + " extends MessageDecoder<" + simple + "> {\n")
                + ("    public " + mdbName + "() {\n")
                + "        super(null, null);\n"
                + "        throw new UnsupportedOperationException(\n"
                + "                \"default consturctor required by MDB lifecycle, but never called\");\n"
                + "    }\n" + "\n" + "    @Inject\n"
                + ("    public " + mdbName + "(@JmsIncoming " + simple + " impl) {\n")
                + ("        super(" + simple + ".class, impl);\n" + "    }\n") //
                + "}\n";
    }
}
