package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc.MembershipFunction;

import java.util.Map;

import static com.bc.dectree.DecTreeDoc.MembershipFunction.RETURN_FALSE;
import static com.bc.dectree.DecTreeDoc.MembershipFunction.RETURN_TRUE;
import static com.bc.dectree.impl.Utilities.*;


/**
 * Defines possible functions which may be used as values for property definitions within a fuzzy set.
 */
class MembershipFunctions {

    static MembershipFunction TRUE(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map0()), RETURN_TRUE);
    }

    static MembershipFunction FALSE(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map0()), RETURN_FALSE);
    }

    static MembershipFunction EQ(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map2(
                "x0", UNDEFINED,
                "dx", 0.0)),
                                      "" +
                        "if (${dx} == 0.0)\n" +
                        "    return x == ${x0} ? 1.0 : 0.0;\n" +
                        "double x1 = ${x0} - ${dx};\n" +
                        "double x2 = ${x0};\n" +
                        "double x3 = ${x0} + ${dx};\n" +
                        "if (x <= x1)\n" +
                        "    return 0.0;\n" +
                        "if (x <= x2)\n" +
                        "    return (x - x1) / (x2 - x1);\n" +
                        "if (x <= x3)\n" +
                        "    return 1.0 - (x - x2) / (x3 - x2);\n" +
                        "return 0.0;"
        );
    }

    static MembershipFunction NE(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map2(
                "x0", UNDEFINED,
                "dx", 0.0)),
                                      "" +
                        "if (${dx} == 0.0)\n" +
                        "    return x != ${x0} ? 1.0 : 0.0;\n" +
                        "double x1 = ${x0} - ${dx};\n" +
                        "double x2 = ${x0};\n" +
                        "double x3 = ${x0} + ${dx};\n" +
                        "if (x <= x1)\n" +
                        "    return 1.0;\n" +
                        "if (x <= x2)\n" +
                        "    return 1.0 - (x - x1) / (x2 - x1);\n" +
                        "if (x <= x3)\n" +
                        "    return (x - x2) / (x3 - x2);\n" +
                        "return 1.0;"
        );
    }

    static MembershipFunction GT(Map<String, Object> params) {
        return MembershipFunctions._greater_op(">", params);
    }


    static MembershipFunction GE(Map<String, Object> params) {
        return MembershipFunctions._greater_op(">=", params);
    }


    static MembershipFunction LT(Map<String, Object> params) {
        return MembershipFunctions._less_op("<", params);
    }

    static MembershipFunction LE(Map<String, Object> params) {
        return MembershipFunctions._less_op("<=", params);
    }

    static MembershipFunction RAMP(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map2(
                "x1", 0.,
                "x2", 1.)),
                                      "" +
                        "if (x <= ${x1})\n" +
                        "    return 0.0;\n" +
                        "if (x <= ${x2})\n" +
                        "    return (x - ${x1}) / (${x2} - ${x1});\n" +
                        "return 1.0;"
        );
    }

    static MembershipFunction INV_RAMP(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map2(
                "x1", 0.,
                "x2", 1.)),
                                      "" +
                        "if (x <= ${x1})\n" +
                        "    return 1.0;\n" +
                        "if (x <= ${x2})\n" +
                        "    return 1.0 - (x - ${x1}) / (${x2} - ${x1});\n" +
                        "return 0.0;"
        );
    }

    static MembershipFunction TRIANGULAR(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map3(
                "x1", 0.0,
                "x2", 0.5,
                "x3", 1.0)),
                                      "" +
                        "if (x <= ${x1})\n" +
                        "    return 0.0;\n" +
                        "if (x <= ${x2})\n" +
                        "    return (x - ${x1}) / (${x2} - ${x1});\n" +
                        "if (x <= ${x3})\n" +
                        "    return 1.0 - (x - ${x2}) / (${x3} - ${x2});\n" +
                        "return 0.0;"
        );
    }


    static MembershipFunction INV_TRIANGULAR(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map3(
                "x1", 0.0,
                "x2", 0.5,
                "x3", 1.0)),
                                      "" +
                        "if (x <= ${x1})\n" +
                        "    return 1.0;\n" +
                        "if (x <= ${x2})\n" +
                        "    return 1.0 - (x - ${x1}) / (${x2} - ${x1});\n" +
                        "if (x <= ${x3})\n" +
                        "    return (x - ${x2}) / (${x3} - ${x2});\n" +
                        "return 1.0;"
        );
    }

    static MembershipFunction TRAPEZOID(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map4(
                "x1", 0.,
                "x2", 1.0 / 3.0,
                "x3", 2.0 / 3.0,
                "x4", 1.0)),
                                      "" +
                        "if (x <= ${x1})\n" +
                        "    return 0.0;\n" +
                        "if (x <= ${x2})\n" +
                        "    return (x - ${x1}) / (${x2} - ${x1});\n" +
                        "if (x <= ${x3})\n" +
                        "    return 1.0;\n" +
                        "if (x <= ${x4})\n" +
                        "    return 1.0 - (x - ${x3}) / (${x4} - ${x3});\n" +
                        "return 0.0;"
        );
    }

    static MembershipFunction INV_TRAPEZOID(Map<String, Object> params) {
        return new MembershipFunction(merge(params, map4(
                "x1", 0.,
                "x2", 1.0 / 3.0,
                "x3", 2.0 / 3.0,
                "x4", 1.0)),
                                      "" +
                        "if (x <= ${x1})\n" +
                        "    return 1.0;\n" +
                        "if (x <= ${x2})\n" +
                        "    return 1.0 - (x - ${x1}) / (${x2} - ${x1});\n" +
                        "if (x <= ${x3})\n" +
                        "    return 0.0;\n" +
                        "if (x <= ${x4})\n" +
                        "    return (x - ${x3}) / (${x4} - ${x3});\n" +
                        "return 1.0;"
        );
    }


    private static MembershipFunction _greater_op(String op, Map<String, Object> params) {
        return new MembershipFunction(merge(params, map2(
                "x0", UNDEFINED,
                "dx", 0.0)),
                                      String.format("" +
                                "if (${dx} == 0.0)\n" +
                                "    return x %s ${x0} ? 1.0 : 0.0;\n" +
                                "double x1 = ${x0} - ${dx};\n" +
                                "double x2 = ${x0} + ${dx};\n" +
                                "if (x <= x1)\n" +
                                "    return 0.0;\n" +
                                "if (x <= x2)\n" +
                                "    return (x - x1) / (x2 - x1);\n" +
                                "return 1.0;",
                        op));
    }


    private static MembershipFunction _less_op(String op, Map<String, Object> params) {
        return new MembershipFunction(merge(params, map2(
                "x0", UNDEFINED,
                "dx", 0.0)),
                                      String.format("" +
                                "if (${dx} == 0.0)\n" +
                                "    return x %s ${x0} ? 1.0 : 0.0;\n" +
                                "double x1 = ${x0} - ${dx};\n" +
                                "double x2 = ${x0} + ${dx};\n" +
                                "if (x <= x1)\n" +
                                "    return 1.0;\n" +
                                "if (x <= x2)\n" +
                                "    return 1.0 - (x - x1) / (x2 - x1);\n" +
                                "return 0.0;",
                        op));
    }

}
