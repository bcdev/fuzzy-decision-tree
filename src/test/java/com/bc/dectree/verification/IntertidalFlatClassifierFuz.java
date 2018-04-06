package com.bc.dectree.verification;

import static java.lang.Math.*;
import com.bc.dectree.DecTreeFunction;

public class IntertidalFlatClassifierFuz implements DecTreeFunction {

    public final int getInputSize() { return 12; }
    public final int getOutputSize() { return 14; }

    public final String[] getInputNames() {
        return new String[] {
            /*0*/ "b1",
            /*1*/ "b2",
            /*2*/ "b3",
            /*3*/ "b4",
            /*4*/ "b5",
            /*5*/ "b7",
            /*6*/ "b8",
            /*7*/ "b12",
            /*8*/ "b13",
            /*9*/ "b14",
            /*10*/ "b16",
            /*11*/ "b19",
        };
    }

    public final String[] getOutputNames() {
        return new String[] {
            /*0*/ "nodata",
            /*1*/ "Wasser",
            /*2*/ "Schill",
            /*3*/ "Muschel",
            /*4*/ "dense2",
            /*5*/ "dense1",
            /*6*/ "Strand",
            /*7*/ "Sand",
            /*8*/ "Misch",
            /*9*/ "Misch2",
            /*10*/ "Schlick",
            /*11*/ "schlick_t",
            /*12*/ "Wasser2",
            /*13*/ "bsum",
        };
    }

    // boolean: (internal)
    private static double boolean_TRUE(double x) {
        return (x <= 0.0) ? 0.0 : (x <= 1.0) ? x : 1.0;
    }

    // boolean: (internal)
    private static double boolean_FALSE(double x) {
        return (x <= 0.0) ? 1.0 : (x <= 1.0) ? 1.0 - x : 0.0;
    }

    // B1: lt(0.85, dx=0.05)
    private static double B1_B1_veg(double x) {
        if (0.05 == 0.0)
            return x < 0.85 ? 1.0 : 0.0;
        double x1 = 0.85 - 0.05;
        double x2 = 0.85 + 0.05;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B1: gt(1.00, dx=0.10)
    private static double B1_B1_strand(double x) {
        if (0.1 == 0.0)
            return x > 1.0 ? 1.0 : 0.0;
        double x1 = 1.0 - 0.1;
        double x2 = 1.0 + 0.1;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B2: gt(0.00, dx=0.01)
    private static double B2_B2_schlick(double x) {
        if (0.01 == 0.0)
            return x > 0.0 ? 1.0 : 0.0;
        double x1 = 0.0 - 0.01;
        double x2 = 0.0 + 0.01;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B3: lt(0.05, dx=0.01)
    private static double B3_B3_sand(double x) {
        if (0.01 == 0.0)
            return x < 0.05 ? 1.0 : 0.0;
        double x1 = 0.05 - 0.01;
        double x2 = 0.05 + 0.01;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B3: lt(0.10, dx=0.01)
    private static double B3_B3_sand2(double x) {
        if (0.01 == 0.0)
            return x < 0.1 ? 1.0 : 0.0;
        double x1 = 0.1 - 0.01;
        double x2 = 0.1 + 0.01;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B3: lt(0.15, dx=0.01)
    private static double B3_B3_misch(double x) {
        if (0.01 == 0.0)
            return x < 0.15 ? 1.0 : 0.0;
        double x1 = 0.15 - 0.01;
        double x2 = 0.15 + 0.01;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B3: lt(0.20, dx=0.01)
    private static double B3_B3_schlick(double x) {
        if (0.01 == 0.0)
            return x < 0.2 ? 1.0 : 0.0;
        double x1 = 0.2 - 0.01;
        double x2 = 0.2 + 0.01;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B4: eq(0.0)
    private static double B4_B4_nodata(double x) {
        if (0.0 == 0.0)
            return x == 0.0 ? 1.0 : 0.0;
        double x1 = 0.0 - 0.0;
        double x2 = 0.0;
        double x3 = 0.0 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        if (x <= x3)
            return 1.0 - (x - x2) / (x3 - x2);
        return 0.0;
    }

    // B5: lt(0.10, dx=0.05)
    private static double B5_B5_wasser(double x) {
        if (0.05 == 0.0)
            return x < 0.1 ? 1.0 : 0.0;
        double x1 = 0.1 - 0.05;
        double x2 = 0.1 + 0.05;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B7: lt(0.50, dx=0.05)
    private static double B7_B7_muschel(double x) {
        if (0.05 == 0.0)
            return x < 0.5 ? 1.0 : 0.0;
        double x1 = 0.5 - 0.05;
        double x2 = 0.5 + 0.05;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B8: gt(0.00, dx=0.01)
    private static double B8_B8_sediment_wasser(double x) {
        if (0.01 == 0.0)
            return x > 0.0 ? 1.0 : 0.0;
        double x1 = 0.0 - 0.01;
        double x2 = 0.0 + 0.01;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: lt(0.09, dx=0.01)
    private static double B8_B8_veg_wasser(double x) {
        if (0.01 == 0.0)
            return x < 0.09 ? 1.0 : 0.0;
        double x1 = 0.09 - 0.01;
        double x2 = 0.09 + 0.01;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B8: gt(0.33, dx=0.02)
    private static double B8_B8_veg(double x) {
        if (0.02 == 0.0)
            return x > 0.33 ? 1.0 : 0.0;
        double x1 = 0.33 - 0.02;
        double x2 = 0.33 + 0.02;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: gt(0.35, dx=0.02)
    private static double B8_B8_muschel_schill(double x) {
        if (0.02 == 0.0)
            return x > 0.35 ? 1.0 : 0.0;
        double x1 = 0.35 - 0.02;
        double x2 = 0.35 + 0.02;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: gt(0.40, dx=0.02)
    private static double B8_B8_muschel_min(double x) {
        if (0.02 == 0.0)
            return x > 0.4 ? 1.0 : 0.0;
        double x1 = 0.4 - 0.02;
        double x2 = 0.4 + 0.02;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: gt(0.45, dx=0.02)
    private static double B8_B8_veg_dicht(double x) {
        if (0.02 == 0.0)
            return x > 0.45 ? 1.0 : 0.0;
        double x1 = 0.45 - 0.02;
        double x2 = 0.45 + 0.02;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: lt(0.85, dx=0.02)
    private static double B8_B8_muschel_max(double x) {
        if (0.02 == 0.0)
            return x < 0.85 ? 1.0 : 0.0;
        double x1 = 0.85 - 0.02;
        double x2 = 0.85 + 0.02;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B16: gt(0.00, dx=0.01)
    private static double B16_B16_sediment_wasser(double x) {
        if (0.01 == 0.0)
            return x > 0.0 ? 1.0 : 0.0;
        double x1 = 0.0 - 0.01;
        double x2 = 0.0 + 0.01;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B19: gt(0.15, dx=0.01)
    private static double B19_B19_muschel(double x) {
        if (0.01 == 0.0)
            return x > 0.15 ? 1.0 : 0.0;
        double x1 = 0.15 - 0.01;
        double x2 = 0.15 + 0.01;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // BSum: gt(0.11, dx=0.02)
    private static double BSum_BSum_schill_1(double x) {
        if (0.02 == 0.0)
            return x > 0.11 ? 1.0 : 0.0;
        double x1 = 0.11 - 0.02;
        double x2 = 0.11 + 0.02;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // BSum: gt(0.13, dx=0.02)
    private static double BSum_BSum_schill_1a(double x) {
        if (0.02 == 0.0)
            return x > 0.13 ? 1.0 : 0.0;
        double x1 = 0.13 - 0.02;
        double x2 = 0.13 + 0.02;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // BSum: gt(0.16, dx=0.01)
    private static double BSum_BSum_schill_2(double x) {
        if (0.01 == 0.0)
            return x > 0.16 ? 1.0 : 0.0;
        double x1 = 0.16 - 0.01;
        double x2 = 0.16 + 0.01;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    public final void apply(double[] inputs, double[] outputs) {
        assert inputs.length >= 12;
        assert outputs.length >= 14;

        double b1 = inputs[0];
        double b2 = inputs[1];
        double b3 = inputs[2];
        double b4 = inputs[3];
        double b5 = inputs[4];
        double b7 = inputs[5];
        double b8 = inputs[6];
        double b12 = inputs[7];
        double b13 = inputs[8];
        double b14 = inputs[9];
        double b16 = inputs[10];
        double b19 = inputs[11];

        double nodata = 0.0;
        double Wasser = 0.0;
        double Schill = 0.0;
        double Muschel = 0.0;
        double dense2 = 0.0;
        double dense1 = 0.0;
        double Strand = 0.0;
        double Sand = 0.0;
        double Misch = 0.0;
        double Misch2 = 0.0;
        double Schlick = 0.0;
        double schlick_t = 0.0;
        double Wasser2 = 0.0;

        double bsum = b12 + b13 + b14;

        double _t0 = 1.0;
        // if b4 is B4_nodata:
        double _t1 = min(_t0, B4_B4_nodata(b4));
        //     nodata: true
        nodata = max(nodata, _t1);
        // else if (b8 is B8_veg and b1 is B1_veg) or b8 is B8_veg_wasser:
        _t1 = min(_t0, 1.0 - _t1);
        double _t2 = min(1.0 - _t1, max(min(B8_B8_veg(b8), B1_B1_veg(b1)), B8_B8_veg_wasser(b8)));
        //     if b5 is B5_wasser:
        double _t3 = min(_t2, B5_B5_wasser(b5));
        //         Wasser: true
        Wasser = max(Wasser, _t3);
        //     else if (b19 is B19_muschel and (b8 is B8_muschel_min and b8 is B8_muschel_max) and b7 is B7_muschel) or (b8 is B8_muschel_min and bsum is BSum_schill_1) or (b8 is B8_muschel_schill and bsum is BSum_schill_2):
        _t3 = min(_t2, 1.0 - _t3);
        double _t4 = min(1.0 - _t3, max(min(B19_B19_muschel(b19), min(min(B8_B8_muschel_min(b8), B8_B8_muschel_max(b8)), B7_B7_muschel(b7))), max(min(B8_B8_muschel_min(b8), BSum_BSum_schill_1(bsum)), min(B8_B8_muschel_schill(b8), BSum_BSum_schill_2(bsum)))));
        //         if bsum is BSum_schill_1a:
        double _t5 = min(_t4, BSum_BSum_schill_1a(bsum));
        //             Schill: true
        Schill = max(Schill, _t5);
        //         else:
        _t5 = min(_t4, 1.0 - _t5);
        //             Muschel: true
        Muschel = max(Muschel, _t5);
        //     else if b8 is B8_veg_dicht:
        _t4 = min(_t3, 1.0 - _t4);
        double _t6 = min(1.0 - _t4, B8_B8_veg_dicht(b8));
        //         dense2: true
        dense2 = max(dense2, _t6);
        //     else:
        _t3 = min(_t2, 1.0 - _t3);
        //         dense1: true
        dense1 = max(dense1, _t3);
        // else if b1 is B1_strand:
        _t2 = min(_t1, 1.0 - _t2);
        double _t7 = min(1.0 - _t2, B1_B1_strand(b1));
        //     Strand: true
        Strand = max(Strand, _t7);
        // else if b3 is B3_sand:
        _t7 = min(_t2, 1.0 - _t7);
        double _t8 = min(1.0 - _t7, B3_B3_sand(b3));
        //     Sand: true
        Sand = max(Sand, _t8);
        // else if b3 is B3_sand2 and b8 is B8_sediment_wasser:
        _t8 = min(_t7, 1.0 - _t8);
        double _t9 = min(1.0 - _t8, min(B3_B3_sand2(b3), B8_B8_sediment_wasser(b8)));
        //     Misch: true
        Misch = max(Misch, _t9);
        // else if b3 is B3_misch and b8 is B8_sediment_wasser:
        _t9 = min(_t8, 1.0 - _t9);
        double _t10 = min(1.0 - _t9, min(B3_B3_misch(b3), B8_B8_sediment_wasser(b8)));
        //     Misch2: true
        Misch2 = max(Misch2, _t10);
        // else if b3 is B3_schlick and b2 is B2_schlick and b8 is B8_sediment_wasser:
        _t10 = min(_t9, 1.0 - _t10);
        double _t11 = min(1.0 - _t10, min(B3_B3_schlick(b3), min(B2_B2_schlick(b2), B8_B8_sediment_wasser(b8))));
        //     Schlick: true
        Schlick = max(Schlick, _t11);
        // else if b16 is B16_sediment_wasser and b8 is B8_sediment_wasser:
        _t11 = min(_t10, 1.0 - _t11);
        double _t12 = min(1.0 - _t11, min(B16_B16_sediment_wasser(b16), B8_B8_sediment_wasser(b8)));
        //     schlick_t: true
        schlick_t = max(schlick_t, _t12);
        // else:
        _t1 = min(_t0, 1.0 - _t1);
        //     Wasser2: true
        Wasser2 = max(Wasser2, _t1);

        outputs[0] = nodata;
        outputs[1] = Wasser;
        outputs[2] = Schill;
        outputs[3] = Muschel;
        outputs[4] = dense2;
        outputs[5] = dense1;
        outputs[6] = Strand;
        outputs[7] = Sand;
        outputs[8] = Misch;
        outputs[9] = Misch2;
        outputs[10] = Schlick;
        outputs[11] = schlick_t;
        outputs[12] = Wasser2;

        outputs[13] = bsum;
    }
}
