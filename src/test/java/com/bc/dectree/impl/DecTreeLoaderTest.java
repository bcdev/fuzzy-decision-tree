package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeFunction;
import org.junit.Test;

import java.io.IOException;

import static com.bc.dectree.impl.TestData.assertAlmostEqual;
import static org.junit.Assert.assertNotNull;

public class DecTreeLoaderTest {
    @Test
    public void testIt() throws IOException {
        DecTreeDoc doc = TestData.getDoc("DecTreeLoaderTest_DecTree");
        DecTreeFunction decTreeFunction = DecTreeLoader.loadCode(doc);
        assertNotNull(decTreeFunction);

        double[] inputs = new double[] {0.8, 1.3};
        double[] outputs = new double[3];
        decTreeFunction.apply(inputs, outputs);
        assertAlmostEqual(0.92, outputs[0]);
        assertAlmostEqual(1.52643375, outputs[1]);
        assertAlmostEqual(0.19189191, outputs[2]);
    }
}
