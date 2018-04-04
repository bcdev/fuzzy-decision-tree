package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class DecTreeCodeGenTest {

    @Test
    public void testSimple()  {
        DecTreeDoc doc = TestData.getDoc("MyDecTree2");
        StringWriter stringWriter = new StringWriter();
        DecTreeCodeGen.writeJava(doc, stringWriter);
    }

    @Test
    public void testParseAndGen() throws IOException, DecTreeParseException {
        File file = new File("./src/test/resources/dectree_test.yml");
        DecTreeDoc doc = DecTreeDocParser.parseDoc(file);
        File root = new File("./temp/java").getAbsoluteFile();
        File sourceFile = new File(root, String.format("com/bc/dectree/%s.java", "DecTreeCodeGenTest_DecTree"));
        DecTreeCodeGen.writeJava(doc, sourceFile);
    }
}
