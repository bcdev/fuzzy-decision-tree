package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DecTreeDocParserTest {

    @Test
    public void testParse() throws IOException, DecTreeParseException {
        File file = new File("./src/test/resources/dectree_test.yml");
        DecTreeDoc doc = DecTreeDocParser.parseDoc(file);
        Assert.assertEquals("DecTree3", doc.name);
        Assert.assertEquals("1.0", doc.version);
    }

    @Test
    public void testParseFunctionParametersSuccess() throws DecTreeParseException {
        Map<String, Double> parameters;

        DecTreeDocParser parser = new DecTreeDocParser("dummy.yml", new HashMap());

        parameters = parser.parseFunctionParameters("  ");
        Assert.assertEquals(0, parameters.size());

        parameters = parser.parseFunctionParameters(" x0 = 13  ");
        Assert.assertEquals(1, parameters.size());
        Assert.assertEquals(new Double(13), parameters.get("x0"));

        parameters = parser.parseFunctionParameters("x1 = 0.1, x2=-2.2");
        Assert.assertEquals(2, parameters.size());
        Assert.assertEquals(new Double(0.1), parameters.get("x1"));
        Assert.assertEquals(new Double(-2.2), parameters.get("x2"));

        parameters = parser.parseFunctionParameters("2.5, dx = 0.05");
        Assert.assertEquals(2, parameters.size());
        Assert.assertEquals(new Double(2.5), parameters.get("param.0"));
        Assert.assertEquals(new Double(0.05), parameters.get("dx"));
    }

    @Test
    public void testParseFunctionParametersFailure() {
        DecTreeDocParser parser = new DecTreeDocParser("dummy.yml", new HashMap());
        parser.pushElement("types");
        parser.pushElement("T1");
        parser.pushElement("P1");

        try {
            parser.parseFunctionParameters(" a, b = 3  ");
            Assert.fail("DecTreeParseException expected");
        } catch (DecTreeParseException e) {
            Assert.assertEquals("dummy.yml: element \"types/T1/P1\": invalid value for parameter at position 1: \"a\"", e.getMessage());
        }

        try {
            parser.parseFunctionParameters(" a b = 3  ");
            Assert.fail("DecTreeParseException expected");
        } catch (DecTreeParseException e) {
            Assert.assertEquals("dummy.yml: element \"types/T1/P1\": invalid parameter name: \"a b\"", e.getMessage());
        }

        try {
            parser.parseFunctionParameters(" a_b = 'u'  ");
            Assert.fail("DecTreeParseException expected");
        } catch (DecTreeParseException e) {
            Assert.assertEquals("dummy.yml: element \"types/T1/P1\": invalid value for parameter \"a_b\": \"'u'\"", e.getMessage());
        }

        try {
            parser.parseFunctionParameters("  = 3  ");
            Assert.fail("DecTreeParseException expected");
        } catch (DecTreeParseException e) {
            Assert.assertEquals("dummy.yml: element \"types/T1/P1\": missing parameter name", e.getMessage());
        }

        parser.popElement();
        parser.popElement();
        parser.popElement();
    }

}
