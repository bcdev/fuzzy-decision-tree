package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeDoc.*;
import com.bc.dectree.DecTreeFunction;
import com.bc.dectree.impl.DecTreeDocParser.Variables;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.bc.dectree.impl.MembershipFunctions.*;
import static com.bc.dectree.impl.Utilities.map2;
import static com.bc.dectree.impl.Utilities.map3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestData {

    public static void testFunction(DecTreeFunction function) {
        assertNotNull(function);
        assertEquals(2, function.getInputSize());
        assertEquals(4, function.getOutputSize());
        double[] inputs = new double[]{0.5, 0.8};
        double[] outputs = new double[4];
        function.apply(inputs, outputs);
        assertAlmostEqual(0.75, outputs[0]);
        assertAlmostEqual(0.2, outputs[1]);
        assertAlmostEqual(0.75, outputs[2]);
        assertAlmostEqual(1.05, outputs[3]);
    }

    public static Variables getVariables(Map<String, Type> types) {
        Variable i1 = new Variable("i1", types.get("T1"));
        Variable i2 = new Variable("i2", types.get("boolean"));
        DerivedVariable d1 = new DerivedVariable("d1", types.get("D1"), "i1 * i1 + i2");
        Variable o1 = new Variable("o1", types.get("boolean"));
        Variable o2 = new Variable("o2", types.get("boolean"));
        Variable o3 = new Variable("o3", types.get("boolean"));

        Map<String, Variable> inputs = new LinkedHashMap<>();
        Map<String, DerivedVariable> derived = new LinkedHashMap<>();
        Map<String, Variable> outputs = new LinkedHashMap<>();

        inputs.put(i1.name, i1);
        inputs.put(i2.name, i2);
        derived.put(d1.name, d1);
        outputs.put(o1.name, o1);
        outputs.put(o2.name, o2);
        outputs.put(o3.name, o3);
        outputs.put(d1.name, d1);

        return new Variables(inputs, derived, outputs);
    }

    public static Map<String, Type> getTypes() {
        Property prop11 = new Property("LOW", INV_RAMP(map2("x1", -3, "x2", 1)), "LOW:...");
        Property prop12 = new Property("MID", TRIANGULAR(map3("x1", -2, "x2", 0, "x3", 2)), "MID:...");
        Property prop13 = new Property("HI", RAMP(map2("x1", 1, "x2", 3)), "HI:...");

        Map<String, Property> properties1 = new LinkedHashMap<>();
        properties1.put(prop11.name, prop11);
        properties1.put(prop12.name, prop12);
        properties1.put(prop13.name, prop13);
        Type type1 = new Type("T1", properties1);

        Property prop21 = new Property("GOOD", INV_RAMP(map2("x1", -3, "x2", 1)), "GOOD:...");
        Property prop22 = new Property("BAD", RAMP(map2("x1", -1, "x2", 3)), "BAD: ...");
        Map<String, Property> properties2 = new LinkedHashMap<>();
        properties2.put(prop21.name, prop21);
        properties2.put(prop22.name, prop22);
        Type type2 = new Type("D1", properties2);

        Map<String, Type> types = new LinkedHashMap<>();
        types.put(Type.NUMBER.name, Type.NUMBER);
        types.put(Type.BOOLEAN.name, Type.BOOLEAN);
        types.put(type1.name, type1);
        types.put(type2.name, type2);
        return types;
    }

    public static DecTreeDoc getDoc(String name) {
        Map<String, Type> types = getTypes();
        Variables variables = getVariables(types);
        Map<String, Variable> inputs = variables.inputs;
        Map<String, DerivedVariable> derived = variables.derived;
        Map<String, Variable> outputs = variables.outputs;

        Variable i1 = inputs.get("i1");
        Variable i2 = inputs.get("i2");
        Variable d1 = derived.get("d1");
        Variable o1 = outputs.get("o1");
        Variable o2 = outputs.get("o2");
        Variable o3 = outputs.get("o3");

        Assert.assertNotNull(i1);
        Assert.assertNotNull(i2);
        Assert.assertNotNull(d1);
        Assert.assertNotNull(o1);
        Assert.assertNotNull(o2);
        Assert.assertNotNull(o3);

        IsExpr comp1 = new IsExpr(i1, i1.type.properties.get("MID"));
        IsExpr comp2 = new IsExpr(d1, d1.type.properties.get("GOOD"));
        IsExpr comp3 = new IsExpr(i2, i2.type.properties.get("TRUE"));
        IsExpr comp4 = new IsExpr(i2, i2.type.properties.get("FALSE"));
        AndExpr and13 = new AndExpr(comp1, comp3);
        OrExpr or24 = new OrExpr(comp2, comp4);

        Assignment assignment1 = new Assignment(o1, true, "");
        Assignment assignment2 = new Assignment(o2, true, "");
        Assignment assignment3 = new Assignment(o3, true, "");

        List<If> ifStatement = new ArrayList<>();
        ifStatement.add(new If(and13, assignment1, ""));
        ifStatement.add(new ElseIf(or24, assignment2, ""));

        List<DecTreeDoc.Statement> rules = new ArrayList<>();
        rules.add(new Rule(ifStatement, new Else(assignment3, "")));

        return new DecTreeDoc(name, "1.0", null, types, inputs, derived, outputs, rules);
    }

    public static void assertAlmostEqual(double expected, double actual) {
        Assert.assertTrue(String.format("expected %s, was %s", expected, actual), Math.abs(expected - actual) < 1e-6);
    }
}
