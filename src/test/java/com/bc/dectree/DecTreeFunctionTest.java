package com.bc.dectree;

import com.bc.dectree.impl.TestData;
import org.junit.Test;

import java.io.IOException;

import static com.bc.dectree.impl.TestData.assertAlmostEqual;
import static com.bc.dectree.impl.TestData.testFunction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DecTreeFunctionTest {
    @Test
    public void testLoadAndApply() throws IOException {
        DecTreeDoc doc = TestData.getDoc("DecTreeFunctionTest_DecTree");
        DecTreeFunction function = DecTreeFunction.load(doc);
        testFunction(function);
    }
}
