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

    @Test
    public void testComp() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof CompExpr);
        Assert.assertSame(((CompExpr) expr).variable, variables.inputs.get("i1"));
        Assert.assertSame(((CompExpr) expr).property, variables.inputs.get("i1").type.properties.get("HI"));
    }

    @Test
    public void testNot() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("not i1 is HI", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof NotExpr);
        Assert.assertTrue(((NotExpr) expr).arg instanceof CompExpr);
        Assert.assertSame(((CompExpr) ((NotExpr) expr).arg).variable, variables.inputs.get("i1"));
        Assert.assertSame(((CompExpr) ((NotExpr) expr).arg).property, variables.inputs.get("i1").type.properties.get("HI"));

        expr = DecTreeExprParser.parseExpr("i1 is not HI", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof NotExpr);
        Assert.assertTrue(((NotExpr) expr).arg instanceof CompExpr);
        Assert.assertSame(((CompExpr) ((NotExpr) expr).arg).variable, variables.inputs.get("i1"));
        Assert.assertSame(((CompExpr) ((NotExpr) expr).arg).property, variables.inputs.get("i1").type.properties.get("HI"));
    }

    @Test
    public void testAnd() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI and i2 is BAD", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof AndExpr);
        Assert.assertTrue(((AndExpr) expr).arg1 instanceof CompExpr);
        Assert.assertTrue(((AndExpr) expr).arg2 instanceof CompExpr);
        Assert.assertSame(((CompExpr) ((AndExpr) expr).arg1).variable, variables.inputs.get("i1"));
        Assert.assertSame(((CompExpr) ((AndExpr) expr).arg1).property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(((CompExpr) ((AndExpr) expr).arg2).variable, variables.inputs.get("i2"));
        Assert.assertSame(((CompExpr) ((AndExpr) expr).arg2).property, variables.inputs.get("i2").type.properties.get("BAD"));
    }

    @Test
    public void testOr() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI or i2 is BAD", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof OrExpr);
        Assert.assertTrue(((OrExpr) expr).arg1 instanceof CompExpr);
        Assert.assertTrue(((OrExpr) expr).arg2 instanceof CompExpr);
        CompExpr arg1 = (CompExpr) ((OrExpr) expr).arg1;
        CompExpr arg2 = (CompExpr) ((OrExpr) expr).arg2;
        Assert.assertSame(arg1.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg1.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg2.variable, variables.inputs.get("i2"));
        Assert.assertSame(arg2.property, variables.inputs.get("i2").type.properties.get("BAD"));
    }

    @Test
    public void testAndOr() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI and i2 is BAD or i1 is LOW", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof OrExpr);
        Assert.assertTrue(((OrExpr) expr).arg1 instanceof AndExpr);
        Assert.assertTrue(((OrExpr) expr).arg2 instanceof CompExpr);
        AndExpr arg1 = (AndExpr) ((OrExpr) expr).arg1;
        CompExpr arg2 = (CompExpr) ((OrExpr) expr).arg2;
        Assert.assertTrue(arg1.arg1 instanceof CompExpr);
        Assert.assertTrue(arg1.arg2 instanceof CompExpr);
        CompExpr arg11 = (CompExpr) arg1.arg1;
        CompExpr arg12 = (CompExpr) arg1.arg2;
        Assert.assertSame(arg11.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg11.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg12.variable, variables.inputs.get("i2"));
        Assert.assertSame(arg12.property, variables.inputs.get("i2").type.properties.get("BAD"));
        Assert.assertSame(arg2.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg2.property, variables.inputs.get("i1").type.properties.get("LOW"));
    }

    @Test
    public void testOrAnd() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("i1 is HI or i2 is BAD and i1 is LOW", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof OrExpr);
        Assert.assertTrue(((OrExpr) expr).arg1 instanceof CompExpr);
        Assert.assertTrue(((OrExpr) expr).arg2 instanceof AndExpr);
        CompExpr arg1 = (CompExpr) ((OrExpr) expr).arg1;
        AndExpr arg2 = (AndExpr) ((OrExpr) expr).arg2;
        Assert.assertTrue(arg2.arg1 instanceof CompExpr);
        Assert.assertTrue(arg2.arg2 instanceof CompExpr);
        CompExpr arg21 = (CompExpr) arg2.arg1;
        CompExpr arg22 = (CompExpr) arg2.arg2;
        Assert.assertSame(arg1.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg1.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg21.variable, variables.inputs.get("i2"));
        Assert.assertSame(arg21.property, variables.inputs.get("i2").type.properties.get("BAD"));
        Assert.assertSame(arg22.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg22.property, variables.inputs.get("i1").type.properties.get("LOW"));
    }

    @Test
    public void testPOrPAnd() throws DecTreeParseException {
        Expr expr = DecTreeExprParser.parseExpr("(i1 is HI or i2 is BAD) and i1 is LOW", variables);
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr instanceof AndExpr);
        Assert.assertTrue(((AndExpr) expr).arg1 instanceof OrExpr);
        Assert.assertTrue(((AndExpr) expr).arg2 instanceof CompExpr);
        OrExpr arg1 = (OrExpr) ((AndExpr) expr).arg1;
        CompExpr arg2 = (CompExpr) ((AndExpr) expr).arg2;
        Assert.assertTrue(arg1.arg1 instanceof CompExpr);
        Assert.assertTrue(arg1.arg2 instanceof CompExpr);
        CompExpr arg11 = (CompExpr) arg1.arg1;
        CompExpr arg12 = (CompExpr) arg1.arg2;
        Assert.assertSame(arg11.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg11.property, variables.inputs.get("i1").type.properties.get("HI"));
        Assert.assertSame(arg12.variable, variables.inputs.get("i2"));
        Assert.assertSame(arg12.property, variables.inputs.get("i2").type.properties.get("BAD"));
        Assert.assertSame(arg2.variable, variables.inputs.get("i1"));
        Assert.assertSame(arg2.property, variables.inputs.get("i1").type.properties.get("LOW"));
    }
}
