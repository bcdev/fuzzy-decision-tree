package com.bc.dectree.verification;

import static java.lang.Math.*;
import com.bc.dectree.DecTreeFunction;

public class IntertidalFlatClassifierOpt implements DecTreeFunction {

    public final int getInputSize() { return 13; }
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
            /*10*/ "b15",
            /*11*/ "b16",
            /*12*/ "b19",
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

    // B1: lt(0.85)
    private static double B1_LT_085(double x) {
        if (0.0 == 0.0)
            return x < 0.85 ? 1.0 : 0.0;
        double x1 = 0.85 - 0.0;
        double x2 = 0.85 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B1: gt(1.0)
    private static double B1_GT_1(double x) {
        if (0.0 == 0.0)
            return x > 1.0 ? 1.0 : 0.0;
        double x1 = 1.0 - 0.0;
        double x2 = 1.0 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B2: gt(0.0)
    private static double B2_GT_0(double x) {
        if (0.0 == 0.0)
            return x > 0.0 ? 1.0 : 0.0;
        double x1 = 0.0 - 0.0;
        double x2 = 0.0 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B3: lt(0.05)
    private static double B3_LT_005(double x) {
        if (0.0 == 0.0)
            return x < 0.05 ? 1.0 : 0.0;
        double x1 = 0.05 - 0.0;
        double x2 = 0.05 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B3: lt(0.1)
    private static double B3_LT_01(double x) {
        if (0.0 == 0.0)
            return x < 0.1 ? 1.0 : 0.0;
        double x1 = 0.1 - 0.0;
        double x2 = 0.1 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B3: lt(0.15)
    private static double B3_LT_015(double x) {
        if (0.0 == 0.0)
            return x < 0.15 ? 1.0 : 0.0;
        double x1 = 0.15 - 0.0;
        double x2 = 0.15 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B3: lt(0.2)
    private static double B3_LT_02(double x) {
        if (0.0 == 0.0)
            return x < 0.2 ? 1.0 : 0.0;
        double x1 = 0.2 - 0.0;
        double x2 = 0.2 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B4: eq(0.0)
    private static double B4_NODATA(double x) {
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

    // B5: lt(0.1)
    private static double B5_LT_01(double x) {
        if (0.0 == 0.0)
            return x < 0.1 ? 1.0 : 0.0;
        double x1 = 0.1 - 0.0;
        double x2 = 0.1 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B7: lt(0.5)
    private static double B7_LT_05(double x) {
        if (0.0 == 0.0)
            return x < 0.5 ? 1.0 : 0.0;
        double x1 = 0.5 - 0.0;
        double x2 = 0.5 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B8: gt(0.0)
    private static double B8_GT_0(double x) {
        if (0.0 == 0.0)
            return x > 0.0 ? 1.0 : 0.0;
        double x1 = 0.0 - 0.0;
        double x2 = 0.0 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: lt(0.09)
    private static double B8_LT_009(double x) {
        if (0.0 == 0.0)
            return x < 0.09 ? 1.0 : 0.0;
        double x1 = 0.09 - 0.0;
        double x2 = 0.09 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B8: gt(0.33)
    private static double B8_GT_033(double x) {
        if (0.0 == 0.0)
            return x > 0.33 ? 1.0 : 0.0;
        double x1 = 0.33 - 0.0;
        double x2 = 0.33 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: gt(0.35)
    private static double B8_GT_035(double x) {
        if (0.0 == 0.0)
            return x > 0.35 ? 1.0 : 0.0;
        double x1 = 0.35 - 0.0;
        double x2 = 0.35 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: gt(0.4)
    private static double B8_GT_04(double x) {
        if (0.0 == 0.0)
            return x > 0.4 ? 1.0 : 0.0;
        double x1 = 0.4 - 0.0;
        double x2 = 0.4 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: gt(0.45)
    private static double B8_GT_045(double x) {
        if (0.0 == 0.0)
            return x > 0.45 ? 1.0 : 0.0;
        double x1 = 0.45 - 0.0;
        double x2 = 0.45 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B8: lt(0.85)
    private static double B8_LT_085(double x) {
        if (0.0 == 0.0)
            return x < 0.85 ? 1.0 : 0.0;
        double x1 = 0.85 - 0.0;
        double x2 = 0.85 + 0.0;
        if (x <= x1)
            return 1.0;
        if (x <= x2)
            return 1.0 - (x - x1) / (x2 - x1);
        return 0.0;
    }

    // B16: gt(0.0)
    private static double B16_GT_0(double x) {
        if (0.0 == 0.0)
            return x > 0.0 ? 1.0 : 0.0;
        double x1 = 0.0 - 0.0;
        double x2 = 0.0 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // B19: gt(0.15)
    private static double B19_GT_015(double x) {
        if (0.0 == 0.0)
            return x > 0.15 ? 1.0 : 0.0;
        double x1 = 0.15 - 0.0;
        double x2 = 0.15 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // BSum: gt(0.11)
    private static double BSum_GT_011(double x) {
        if (0.0 == 0.0)
            return x > 0.11 ? 1.0 : 0.0;
        double x1 = 0.11 - 0.0;
        double x2 = 0.11 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // BSum: gt(0.13)
    private static double BSum_GT_013(double x) {
        if (0.0 == 0.0)
            return x > 0.13 ? 1.0 : 0.0;
        double x1 = 0.13 - 0.0;
        double x2 = 0.13 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    // BSum: gt(0.16)
    private static double BSum_GT_016(double x) {
        if (0.0 == 0.0)
            return x > 0.16 ? 1.0 : 0.0;
        double x1 = 0.16 - 0.0;
        double x2 = 0.16 + 0.0;
        if (x <= x1)
            return 0.0;
        if (x <= x2)
            return (x - x1) / (x2 - x1);
        return 1.0;
    }

    public final void apply(double[] inputs, double[] outputs) {
        assert inputs.length >= 13;
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
        double b15 = inputs[10];
        double b16 = inputs[11];
        double b19 = inputs[12];

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
        // if b4 is NODATA:
        double _t1 = min(_t0, B4_NODATA(b4));
        //     nodata: true
        nodata = max(nodata, _t1);
        // else if (b8 is GT_033 and b1 is LT_085) or b8 is LT_009:
        _t1 = min(_t0, 1.0 - _t1);
        double _t2 = min(1.0 - _t1, max(min(B8_GT_033(b8), B1_LT_085(b1)), B8_LT_009(b8)));
        //     if b5 is LT_01:
        double _t3 = min(_t2, B5_LT_01(b5));
        //         Wasser: true
        Wasser = max(Wasser, _t3);
        //     else if (b19 is GT_015 and (b8 is GT_04 and b8 is LT_085) and b7 is LT_05) or (b8 is GT_04 and bsum is GT_011) or (b8 is GT_035 and bsum is GT_016):
        _t3 = min(_t2, 1.0 - _t3);
        double _t4 = min(1.0 - _t3, max(min(B19_GT_015(b19), min(min(B8_GT_04(b8), B8_LT_085(b8)), B7_LT_05(b7))), max(min(B8_GT_04(b8), BSum_GT_011(bsum)), min(B8_GT_035(b8), BSum_GT_016(bsum)))));
        //         if bsum is GT_013:
        double _t5 = min(_t4, BSum_GT_013(bsum));
        //             Schill: true
        Schill = max(Schill, _t5);
        //         else:
        _t5 = min(_t4, 1.0 - _t5);
        //             Muschel: true
        Muschel = max(Muschel, _t5);
        //     else if b8 is GT_045:
        _t4 = min(_t3, 1.0 - _t4);
        double _t6 = min(1.0 - _t4, B8_GT_045(b8));
        //         dense2: true
        dense2 = max(dense2, _t6);
        //     else:
        _t3 = min(_t2, 1.0 - _t3);
        //         dense1: true
        dense1 = max(dense1, _t3);
        // else if b1 is GT_1:
        _t2 = min(_t1, 1.0 - _t2);
        double _t7 = min(1.0 - _t2, B1_GT_1(b1));
        //     Strand: true
        Strand = max(Strand, _t7);
        // else if b3 is LT_005:
        _t7 = min(_t2, 1.0 - _t7);
        double _t8 = min(1.0 - _t7, B3_LT_005(b3));
        //     Sand: true
        Sand = max(Sand, _t8);
        // else if b3 is LT_01 and b8 is GT_0:
        _t8 = min(_t7, 1.0 - _t8);
        double _t9 = min(1.0 - _t8, min(B3_LT_01(b3), B8_GT_0(b8)));
        //     Misch: true
        Misch = max(Misch, _t9);
        // else if b3 is LT_015 and b8 is GT_0:
        _t9 = min(_t8, 1.0 - _t9);
        double _t10 = min(1.0 - _t9, min(B3_LT_015(b3), B8_GT_0(b8)));
        //     Misch2: true
        Misch2 = max(Misch2, _t10);
        // else if b3 is LT_02 and b2 is GT_0 and b8 is GT_0:
        _t10 = min(_t9, 1.0 - _t10);
        double _t11 = min(1.0 - _t10, min(B3_LT_02(b3), min(B2_GT_0(b2), B8_GT_0(b8))));
        //     Schlick: true
        Schlick = max(Schlick, _t11);
        // else if b16 is GT_0 and b8 is GT_0:
        _t11 = min(_t10, 1.0 - _t11);
        double _t12 = min(1.0 - _t11, min(B16_GT_0(b16), B8_GT_0(b8)));
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
