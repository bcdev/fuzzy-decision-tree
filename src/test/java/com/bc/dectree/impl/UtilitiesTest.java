package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UtilitiesTest {
    @Test
    public void testGetCodeSourceWithJar() {
        File codeSource = Utilities.getCodeSource(String.class);
        assertNotNull(codeSource);
        assertEquals(codeSource.getName(), "rt.jar");
    }

    @Test
    public void testGetCodeSource() {
        File codeSource = Utilities.getCodeSource(DecTreeDoc.class);
        assertNotNull(codeSource);
        assertEquals(codeSource.getName(), "classes");
        assertNotNull(codeSource.getParentFile());
        assertEquals(codeSource.getParentFile().getName(), "target");
    }
}
