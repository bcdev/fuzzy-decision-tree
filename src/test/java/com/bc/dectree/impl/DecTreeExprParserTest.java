package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc.*;
import com.bc.dectree.DecTreeParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;


public class DecTreeExprParserTest {

    private DecTreeDocParser.Variables variables;

    @Before
    public void setUp() {
        Map<String, Type> types = TestData.getTypes();
        variables = TestData.getVariables(types);
    }

    private void assertPropDefined(Expr expr, String varName, String propName) {
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof IsExpr);
        Assert.assertSame(((IsExpr) expr).variable, variables.inputs.get(varName));
        Assert.assertSame(((IsExpr) expr).property, variables.inputs.get(varName).type.properties.get(propName));
    }

    @Test
    public void testComp() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI", variables);
        assertPropDefined(expr, "i1", "HI");
    }

    @Test
    public void testCompPredefinedBoolean() throws DecTreeParseException {
        assertPropDefined(DecTreeExprParser.parseExpr("i2 is TRUE", variables), "i2", "TRUE");
        assertPropDefined(DecTreeExprParser.parseExpr("i2 is FALSE", variables), "i2",  "FALSE");
    }

    @Test
    public void testNot() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("not i1 is HI", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof NotExpr);
        Assert.assertTrue(((NotExpr) expr).arg instanceof IsExpr);
        Assert.assertSame(((IsExpr) ((NotExpr) expr).arg).variable, variables.inputs.get("i1"));
        Assert.assertSame(((IsExpr) ((NotExpr) expr).arg).property, variables.inputs.get("i1").type.properties.get("HI"));

        expr = DecTreeExprParser.parseExpr("i1 is not HI", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof NotExpr);
        Assert.assertTrue(((NotExpr) expr).arg instanceof IsExpr);
        Assert.assertSame(((IsExpr) ((NotExpr) expr).arg).variable, variables.inputs.get("i1"));
        Assert.assertSame(((IsExpr) ((NotExpr) expr).arg).property, variables.inputs.get("i1").type.properties.get("HI"));
    }

    @Test
    public void testAnd() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI and d1 is BAD", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof AndExpr);
        Assert.assertTrue(((AndExpr) expr).arg1 instanceof IsExpr);
        Assert.assertTrue(((AndExpr) expr).arg2 instanceof IsExpr);
        Assert.assertSame(((IsExpr) ((AndExpr) expr).arg1).variable, variables.inputs.get("i1"));
        Assert.assertSame(((IsExpr) ((AndExpr) expr).arg1).property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(((IsExpr) ((AndExpr) expr).arg2).variable, variables.derived.get("d1"));
        Assert.assertSame(((IsExpr) ((AndExpr) expr).arg2).property, variables.derived.get("d1").type.properties.get("BAD"));
    }

    @Test
    public void testOr() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI or d1 is BAD", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof OrExpr);
        Assert.assertTrue(((OrExpr) expr).arg1 instanceof IsExpr);
        Assert.assertTrue(((OrExpr) expr).arg2 instanceof IsExpr);
        IsExpr arg1 = (IsExpr) ((OrExpr) expr).arg1;
        IsExpr arg2 = (IsExpr) ((OrExpr) expr).arg2;
        Assert.assertSame(arg1.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg1.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg2.variable, variables.derived.get("d1"));
        Assert.assertSame(arg2.property, variables.derived.get("d1").type.properties.get("BAD"));
    }

    @Test
    public void testAndOr() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI and d1 is BAD or i1 is LOW", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof OrExpr);
        Assert.assertTrue(((OrExpr) expr).arg1 instanceof AndExpr);
        Assert.assertTrue(((OrExpr) expr).arg2 instanceof IsExpr);
        AndExpr arg1 = (AndExpr) ((OrExpr) expr).arg1;
        IsExpr arg2 = (IsExpr) ((OrExpr) expr).arg2;
        Assert.assertTrue(arg1.arg1 instanceof IsExpr);
        Assert.assertTrue(arg1.arg2 instanceof IsExpr);
        IsExpr arg11 = (IsExpr) arg1.arg1;
        IsExpr arg12 = (IsExpr) arg1.arg2;
        Assert.assertSame(arg11.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg11.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg12.variable, variables.derived.get("d1"));
        Assert.assertSame(arg12.property, variables.derived.get("d1").type.properties.get("BAD"));
        Assert.assertSame(arg2.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg2.property, variables.inputs.get("i1").type.properties.get("LOW"));
    }

    @Test
    public void testOrAnd() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI or d1 is BAD and i1 is LOW", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof OrExpr);
        Assert.assertTrue(((OrExpr) expr).arg1 instanceof IsExpr);
        Assert.assertTrue(((OrExpr) expr).arg2 instanceof AndExpr);
        IsExpr arg1 = (IsExpr) ((OrExpr) expr).arg1;
        AndExpr arg2 = (AndExpr) ((OrExpr) expr).arg2;
        Assert.assertTrue(arg2.arg1 instanceof IsExpr);
        Assert.assertTrue(arg2.arg2 instanceof IsExpr);
        IsExpr arg21 = (IsExpr) arg2.arg1;
        IsExpr arg22 = (IsExpr) arg2.arg2;
        Assert.assertSame(arg1.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg1.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg21.variable, variables.derived.get("d1"));
        Assert.assertSame(arg21.property, variables.derived.get("d1").type.properties.get("BAD"));
        Assert.assertSame(arg22.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg22.property, variables.inputs.get("i1").type.properties.get("LOW"));
    }

    @Test
    public void testPOrPAnd() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("(i1 is HI or d1 is BAD) and i1 is LOW", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof AndExpr);
        Assert.assertTrue(((AndExpr) expr).arg1 instanceof OrExpr);
        Assert.assertTrue(((AndExpr) expr).arg2 instanceof IsExpr);
        OrExpr arg1 = (OrExpr) ((AndExpr) expr).arg1;
        IsExpr arg2 = (IsExpr) ((AndExpr) expr).arg2;
        Assert.assertTrue(arg1.arg1 instanceof IsExpr);
        Assert.assertTrue(arg1.arg2 instanceof IsExpr);
        IsExpr arg11 = (IsExpr) arg1.arg1;
        IsExpr arg12 = (IsExpr) arg1.arg2;
        Assert.assertSame(arg11.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg11.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg12.variable, variables.derived.get("d1"));
        Assert.assertSame(arg12.property, variables.derived.get("d1").type.properties.get("BAD"));
        Assert.assertSame(arg2.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg2.property, variables.inputs.get("i1").type.properties.get("LOW"));
    }
}
