package com.bc.dectree;

import java.io.File;
import java.io.IOException;
import com.bc.dectree.impl.DecTreeCodeGen;
import com.bc.dectree.impl.Utilities;

import static com.bc.dectree.impl.Utilities.getSimpleClassName;

public class DecTreeMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.printf("Usage: %s <dectree-yaml-file>\n", DecTreeMain.class.getSimpleName());
            System.exit(1);
        }

        File yamlFile = new File(args[0]);
        File dir = yamlFile.getAbsoluteFile().getParentFile();
        try {
            DecTreeDoc doc = DecTreeDoc.parse(yamlFile);
            DecTreeCodeGen.writeJava(doc, new File(dir, getSimpleClassName(doc.name) + ".java"));
        } catch (IOException | DecTreeParseException e) {
            System.err.printf("Error: %s\n", e.getMessage());
        }
    }
}
