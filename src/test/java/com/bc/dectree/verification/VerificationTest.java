package com.bc.dectree.verification;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.bc.dectree.verification.DataFrame.readCsv;


public class VerificationTest {

    public static final String INPUT_TXT = "./verification/verification_input.txt";
    public static final String EXPECTED_TXT = "./verification/verification_expected.txt";
    public static final String OUTPUT_TXT = "./verification/verification_output.txt";

    static final String[][] INPUT_NAMES = {
            {"b1", "sand-tr_abundance"},
            {"b2", "sand-wc_abundance"},
            {"b3", "schatten_abundance"},
            {"b4", "summary_error"},
            {"b5", "steigung_red_nIR"},
            {"b6", "steigung_nIR_SWIR1"},
            {"b7", "flh"},
            {"b8", "ndvi"},
            {"b12", "reflec_483"},
            {"b13", "reflec_561"},
            {"b14", "reflec_655"},
            //{"b15", "reflec_865"},
            {"b16", "reflec_1609"},
            {"b19", "muschelindex"},
            //{"bsum", "reflec_sum"},
    };

    static final String[] OUTPUT_NAMES = {
            "nodata",
            "Wasser",
            "Schill",
            "Muschel",
            "dense2",
            "dense1",
            "Strand",
            "Sand",
            "Misch",
            "Misch2",
            "Schlick",
            "schlick_t",
            "Wasser2",
            "bsum",
    };

    @Test
    public void verifyWeGetKerstinsResults() throws IOException {
        Map<String, Integer> classes = new HashMap<>();
        classes.put("Sand", 1);
        classes.put("Misch", 2);
        classes.put("Misch2", 3);
        classes.put("Schlick", 4);
        classes.put("schlick_t", 5);
        classes.put("dense1", 6);
        classes.put("dense2", 7);
        classes.put("Muschel", 8);
        classes.put("Strand", 9);
        classes.put("Wasser", 10);
        classes.put("Wasser2", 12);
        classes.put("nodata", 11);
        classes.put("Schill", 13);

        DataFrame inputFrame = readCsv(INPUT_TXT);
        DataFrame expectedFrame = readCsv(EXPECTED_TXT);

        inputFrame.sortValues("Label");
        expectedFrame.sortValues("Label");

        DataFrame outputDataFrame = new DataFrame(OUTPUT_NAMES);

        IntertidalFlatClassifier function = new IntertidalFlatClassifier();
        double[] inputs = new double[INPUT_NAMES.length];
        double[] outputs = new double[OUTPUT_NAMES.length];
        for (int rowIndex = 0; rowIndex < inputFrame.getNumRows(); rowIndex++) {
            for (int i = 0; i < INPUT_NAMES.length; i++) {
                String[] inputName = INPUT_NAMES[i];
                inputs[i] = inputFrame.getDouble(inputName[1], rowIndex);
            }

            function.apply(inputs, outputs);
            outputDataFrame.addRow(outputs);

            int actualClass = -1;
            for (int i = 0; i < outputs.length - 1; i++) {
                double output = outputs[i];
                if (output > 0.5) {
                    actualClass = classes.get(OUTPUT_NAMES[i]);
                }
            }

            int expectedClass = (int) expectedFrame.getDouble("Band_1", rowIndex);
            if (actualClass != expectedClass) {
                Assert.assertEquals("row " + (rowIndex + 1), expectedClass, actualClass);
            }
        }

        outputDataFrame.writeCsv(OUTPUT_TXT);
    }

}
