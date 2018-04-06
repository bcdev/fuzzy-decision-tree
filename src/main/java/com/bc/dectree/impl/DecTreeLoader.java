package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeFunction;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

import static com.bc.dectree.impl.Utilities.getPackageName;
import static com.bc.dectree.impl.Utilities.getSimpleClassName;


public class DecTreeLoader {
    private final File rootDir;
    private final File packageDir;
    private final File javaFile;
    private final File classFile;
    private final String className;

    private DecTreeLoader(File rootDir, String className) {
        this.rootDir = rootDir;
        this.className = className;
        String packageName = getPackageName(className);
        String simpleClassName = getSimpleClassName(className);
        this.packageDir = new File(rootDir, packageName.replace('.', '/'));
        this.javaFile = new File(packageDir, simpleClassName + ".java");
        this.classFile = new File(packageDir, simpleClassName + ".class");
    }

    public static DecTreeFunction loadCode(DecTreeDoc doc) throws IOException {
        File rootDir = Files.createTempDirectory("java-").toFile();
        DecTreeLoader loader = new DecTreeLoader(rootDir, doc.name);
        DecTreeCodeGen.writeJava(doc, loader.javaFile, false);
        loader.javaFile.deleteOnExit();
        loader.classFile.deleteOnExit();
        rootDir.deleteOnExit();
        return loader.load();
    }

    private DecTreeFunction load() throws IOException {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, javaFile.getPath());
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{rootDir.toURI().toURL()});
            Class<?> cls = Class.forName(String.format("%s", className), true, classLoader);
            return (DecTreeFunction) cls.newInstance();
        } catch (ClassNotFoundException
                | ClassCastException
                | InstantiationException
                | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}