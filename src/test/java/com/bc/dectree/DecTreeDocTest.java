package com.bc.dectree;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class DecTreeDocTest {

    @Test
    public void testParseDoc() throws IOException, DecTreeParseException {
        File yamlFile = new File("./src/test/resources/dectree_test.yml");
        DecTreeDoc doc = DecTreeDoc.parse(yamlFile);

        assertNotNull(doc);

        assertEquals("DecTree3", doc.name);
        assertEquals("1.0", doc.version);

        assertNotNull(doc.types);
        assertEquals(4, doc.types.size());
        assertNotNull(doc.types.get("number"));
        assertNotNull(doc.types.get("boolean"));
        assertNotNull(doc.types.get("Radiance"));
        assertNotNull(doc.types.get("Glint"));
    }
}
