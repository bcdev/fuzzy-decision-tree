package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeFunction;
import org.junit.Test;

import java.io.IOException;

import static com.bc.dectree.impl.TestData.assertAlmostEqual;
import static com.bc.dectree.impl.TestData.testFunction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DecTreeLoaderTest {
    @Test
    public void testIt() throws IOException {
        DecTreeDoc doc = TestData.getDoc("DecTreeLoaderTest_DecTree");
        DecTreeFunction function = DecTreeLoader.loadCode(doc);
        testFunction(function);
    }
}
