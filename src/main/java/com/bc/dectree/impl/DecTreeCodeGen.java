package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeDoc.Property;
import com.bc.dectree.DecTreeDoc.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bc.dectree.impl.Utilities.getPackageName;
import static com.bc.dectree.impl.Utilities.getSimpleClassName;

public class DecTreeCodeGen {
    private final DecTreeDoc doc;

    private DecTreeCodeGen(DecTreeDoc doc) {
        this.doc = doc;
    }

    public static void writeJava(DecTreeDoc doc, File javaFile) throws IOException {
        DecTreeCodeGen gen = new DecTreeCodeGen(doc);
        //noinspection ResultOfMethodCallIgnored
        javaFile.getParentFile().mkdirs();
        gen.genCode(new FileWriter(javaFile));
    }

    static void writeJava(DecTreeDoc doc, Writer writer) {
        DecTreeCodeGen gen = new DecTreeCodeGen(doc);
        gen.genCode(writer);
    }

    private void genCode(Writer writer) {
        List<String> lines = genCode();
        try (PrintWriter w = new PrintWriter(writer)) {
            for (String line : lines) {
                w.println(line);
            }
        }
    }

    private List<String> genCode() {
        String indent = "    ";
        String indent2 = indent + indent;

        List<String> lines = new ArrayList<>();

        String packageName = getPackageName(doc.name);
        if (!packageName.isEmpty()) {
            lines.add(String.format("package %s;", packageName));
            lines.add("");
        }

        lines.add("import static java.lang.Math.*;");
        lines.add("import com.bc.dectree.DecTreeFunction;");

        lines.add("");
        lines.add(String.format("public class %s implements DecTreeFunction {", getSimpleClassName(doc.name)));

        for (String typeName : doc.types.keySet()) {
            Type type = doc.types.get(typeName);
            for (String propertyName : type.properties.keySet()) {
                Property property = type.properties.get(propertyName);
                lines.add("");
                lines.add(indent + String.format("// %s/%s", type.name, property.code));
                lines.add(indent + String.format("private static double %s_%s(double x) {", type.name, property.name));
                lines.addAll(property.membershipFunction.genCode().stream().map(s -> indent2 + s).collect(Collectors.toList()));
                lines.add(indent + "}");
            }
        }

        int numInputs = doc.inputs.size();
        int numDerived = doc.derived.size();
        int numOutputs = doc.outputs.size();

        lines.add("");
        lines.add(indent + "public void apply(double[] inputs, double[] outputs) {");
        lines.add(indent2 + String.format("assert inputs.length >= %s;", numInputs));
        lines.add(indent2 + String.format("assert outputs.length >= %s;", numOutputs + numDerived));

        if (numInputs > 0) {
            lines.add("");
            int inputIndex = 0;
            for (String name : doc.inputs.keySet()) {
                lines.add(indent2 + String.format("double %s = inputs[%s];", name, inputIndex));
                inputIndex++;
            }
        }

        if (numOutputs > 0) {
            lines.add("");
            for (String name : doc.outputs.keySet()) {
                lines.add(indent2 + String.format("double %s = 0.0;", name));
            }
        }

        if (numDerived > 0) {
            lines.add("");
            for (String name : doc.derived.keySet()) {
                lines.add(indent2 + String.format("double %s = %s;", name, doc.derived.get(name).expression));
            }
        }

        ContextImpl ctx = new ContextImpl();
        lines.add("");
        lines.add(indent2 + String.format("double %s = 1.0;", ctx.getCurrent()));
        for (DecTreeDoc.Statement statement : doc.rules) {
            lines.addAll(statement.genCode(ctx).stream().map(s -> indent2 + s).collect(Collectors.toList()));
        }

        int outputIndex = 0;

        if (numOutputs > 0) {
            lines.add("");
            for (String name : doc.outputs.keySet()) {
                lines.add(indent2 + String.format("outputs[%s] = %s;", outputIndex, name));
                outputIndex++;
            }
        }

        if (numDerived > 0) {
            lines.add("");
            for (String name : doc.derived.keySet()) {
                lines.add(indent2 + String.format("outputs[%s] = %s;", outputIndex, name));
                outputIndex++;
            }
        }

        lines.add(indent + "}");
        lines.add("}");

        return lines;

    }
}