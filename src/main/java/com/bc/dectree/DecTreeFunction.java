package com.bc.dectree;

import com.bc.dectree.impl.DecTreeLoader;

import java.io.IOException;

/**
 * The decision tree function.
 */
public interface DecTreeFunction {

    /**
     * Loads a decision tree function from a {@link DecTreeDoc}.
     *
     * @param doc A {@link DecTreeDoc} instance.
     * @throws IOException if an I/O error occurs during code generation.
     */
    static DecTreeFunction load(DecTreeDoc doc) throws IOException {
        return DecTreeLoader.loadCode(doc);
    }

    /**
     * Applies the decision tree function to the given {@code inputs} and computes {@code outputs}.
     *
     * @param inputs  The inputs
     * @param outputs The outputs
     */
    void apply(double[] inputs, double[] outputs);
}
