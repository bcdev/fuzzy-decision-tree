package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeFunction;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import static com.bc.dectree.impl.Utilities.getPackageName;
import static com.bc.dectree.impl.Utilities.getSimpleClassName;


public class DecTreeLoader {

    public static DecTreeFunction loadCode(DecTreeDoc doc) throws IOException {
        String className = doc.name;
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StringWriter writer = new StringWriter();
            DecTreeCodeGen.writeJava(doc, writer, true);
            JavaFileObject file = new MemoryJavaFileObject(className, writer.toString());
            StringWriter out = new StringWriter(16 * 1024);
            ByteArrayOutputStream err = new ByteArrayOutputStream(16 * 1024);
            Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(file);
            JavaCompiler.CompilationTask task = compiler.getTask(out, null, diagnostics, null, null, compilationUnits);
            boolean success = task.call();
            if (!success) {
                throw new IllegalStateException("compilation failed");
            }
            Class<?> cls = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            return (DecTreeFunction) cls.newInstance();
        } catch (ClassNotFoundException
                | ClassCastException
                | InstantiationException
                | IllegalAccessException e) {
            String msg = String.format("loading of %s failed: %s", className, e.getMessage());
            throw new IllegalStateException(msg, e);
        }
    }

    static class MemoryJavaFileObject extends SimpleJavaFileObject {
        private final String code;

        MemoryJavaFileObject(String name, String code) {
            super(URI.create(String.format("string:///%s/%s",
                                           name.replace('.', '/'),
                                           Kind.SOURCE.extension)),
                  Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}