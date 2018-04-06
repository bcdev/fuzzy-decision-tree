package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeDoc.*;
import com.bc.dectree.impl.DecTreeDocParser.Variables;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.bc.dectree.impl.Utilities.*;
import static com.bc.dectree.impl.MembershipFunctions.*;

public class TestData {
    public static Variables getVariables(Map<String, Type> types) {
        return new Variables(getInputs(types), getDerived(types), getOutputs(types));
    }

    public static DecTreeDoc getDoc(String name) {
        Map<String, Type> types = getTypes();
        Variables variables = getVariables(types);
        Map<String, Variable> inputs = variables.inputs;
        Map<String, DerivedVariable> derived = variables.derived;
        Map<String, Variable> outputs = variables.outputs;

        Assignment assignment1 = new Assignment(outputs.get("o1"), true, "");
        Assignment assignment2 = new Assignment(outputs.get("o1"), false, "");
        Assignment assignment3 = new Assignment(outputs.get("o1"), false, "");

        CompExpr comp1 = new CompExpr(inputs.get("i1"), inputs.get("i1").type.properties.get("HI"));
        CompExpr comp2 = new CompExpr(inputs.get("i2"), inputs.get("i2").type.properties.get("GOOD"));
        CompExpr comp3 = new CompExpr(inputs.get("i1"), inputs.get("i1").type.properties.get("LOW"));
        AndExpr and12 = new AndExpr(comp1, comp2);

        List<If> ifStatement = new ArrayList<>();
        ifStatement.add(new If(and12, assignment1,""));
        ifStatement.add(new ElseIf(comp3, assignment2, ""));
        Else elseStatement = new Else(assignment3, "");

        List<DecTreeDoc.Statement> rules = new ArrayList<>();
        rules.add(new IfElse(ifStatement, elseStatement));

        return new DecTreeDoc(name, "1.0", null, types, inputs, derived, outputs, rules);
    }

    public static Map<String, Type> getTypes() {
        Property prop11 = new Property("LOW", INV_RAMP(map2("x1", -10, "x2", 0)), "LOW=...");
        Property prop12 = new Property("MID", TRIANGULAR(map3("x1", -10, "x2", 0, "x3", 10)), "MID=...");
        Property prop13 = new Property("HI", RAMP(map2("x1", 0, "x2", 10)), "HI=...");

        Map<String, Property> properties1 = new LinkedHashMap<>();
        properties1.put(prop11.name, prop11);
        properties1.put(prop12.name, prop12);
        properties1.put(prop13.name, prop13);
        Type type1 = new Type("T1", properties1);

        Property prop21 = new Property("GOOD", LE(map2("x0", 0, "dx", 2)), "GOOD=...");
        Property prop22 = new Property("BAD", GE(map2("x0", 10, "dx", 5)), "BAD=...");
        Map<String, Property> properties2 = new LinkedHashMap<>();
        properties2.put(prop21.name, prop21);
        properties2.put(prop22.name, prop22);
        Type type2 = new Type("T2", properties2);

        Map<String, Property> properties3 = new LinkedHashMap<>();
        Property prop31 = new Property("ON", TRUE(map0()), "ON=...");
        Property prop32 = new Property("OFF", FALSE(map0()), "OFF=...");
        properties3.put(prop31.name, prop31);
        properties3.put(prop32.name, prop32);
        Type type3 = new Type("T3", properties3);

        Map<String, Type> types = new LinkedHashMap<>();
        types.put(Type.NUMBER.name, Type.NUMBER);
        types.put(Type.BOOLEAN.name, Type.BOOLEAN);
        types.put(type1.name, type1);
        types.put(type2.name, type2);
        types.put(type3.name, type3);
        return types;
    }

    private static Map<String, Variable> getOutputs(Map<String, Type> types) {
        Variable output1 = new Variable("o1", types.get("T3"));

        Map<String, Variable> outputs = new LinkedHashMap<>();
        outputs.put(output1.name, output1);
        return outputs;
    }

    private static Map<String, DerivedVariable> getDerived(Map<String, Type> types) {
        DerivedVariable derived1 = new DerivedVariable("d1", types.get("T1"), "sqrt(i1 * i1 + i2 * i2)");
        DerivedVariable derived2 = new DerivedVariable("d2", types.get("T1"), "sin(i1) * cos(i2)");
        Map<String, DerivedVariable> deriveds = new LinkedHashMap<>();
        deriveds.put(derived1.name, derived1);
        deriveds.put(derived2.name, derived2);
        return deriveds;
    }

    private static Map<String, Variable> getInputs(Map<String, Type> types) {
        Variable input1 = new Variable("i1", types.get("T1"));
        Variable input2 = new Variable("i2", types.get("T2"));
        Variable input3 = new Variable("i3", types.get("boolean"));

        Map<String, Variable> inputs = new LinkedHashMap<>();
        inputs.put(input1.name, input1);
        inputs.put(input2.name, input2);
        inputs.put(input3.name, input3);
        return inputs;
    }

    public static void assertAlmostEqual(double expected, double actual) {
        Assert.assertTrue(String.format("expected %s, was %s", expected, actual), Math.abs(expected - actual) < 1e-6);
    }
}
