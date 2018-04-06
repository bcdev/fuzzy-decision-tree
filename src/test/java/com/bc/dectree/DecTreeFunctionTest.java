package com.bc.dectree;

import com.bc.dectree.impl.TestData;
import org.junit.Test;

import java.io.IOException;

import static com.bc.dectree.impl.TestData.assertAlmostEqual;
import static org.junit.Assert.assertNotNull;

public class DecTreeFunctionTest {
    @Test
    public void testLoadAndApply() throws IOException {
        DecTreeDoc doc = TestData.getDoc("DecTreeFunctionTest_DecTree");
        DecTreeFunction function = DecTreeFunction.load(doc);
        assertNotNull(function);
        double[] inputs = new double[]{0.8, 1.3, 0.9};
        double[] outputs = new double[3];
        function.apply(inputs, outputs);
        assertAlmostEqual(1.0, outputs[0]);
        assertAlmostEqual(1.52643375, outputs[1]);
        assertAlmostEqual(0.19189191, outputs[2]);
    }
}
