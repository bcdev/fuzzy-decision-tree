package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeDoc.*;
import com.bc.dectree.DecTreeParseException;
import com.bc.dectree.impl.DecTreeDocParser.Variables;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;


class DecTreeExprParser {
    private StreamTokenizer tokenizer;
    private Variables variables;

    private DecTreeExprParser(String expr, Variables variables) {
        this.tokenizer = new StreamTokenizer(new StringReader(expr));
        this.tokenizer.resetSyntax();
        this.tokenizer.parseNumbers();
        this.tokenizer.whitespaceChars(0, 32);
        this.tokenizer.wordChars('a', 'z');
        this.tokenizer.wordChars('A', 'Z');
        this.tokenizer.wordChars('0', '9');
        this.tokenizer.wordChars('_', '_');
        this.variables = variables;
    }

    public static Expr parseExpr(String expr, Variables variables) throws DecTreeParseException {
        DecTreeExprParser parser = new DecTreeExprParser(expr, variables);
        return parser.parse0();
    }

    private Expr parse0() throws DecTreeParseException {
        Expr expr = parse();
        if (expr == null) {
            throw new DecTreeParseException("expression expected");
        }
        int token = nextToken();
        if (token == StreamTokenizer.TT_WORD) {
            throw new DecTreeParseException(String.format("unexpected word  \"%s\"", tokenizer.sval));
        }
        if (token == StreamTokenizer.TT_NUMBER) {
            throw new DecTreeParseException(String.format("unexpected number '%s'", tokenizer.nval));
        }
        if (token != StreamTokenizer.TT_EOF) {
            throw new DecTreeParseException(String.format("unexpected token '%c'", token));
        }
        return expr;
    }

    private Expr parse() throws DecTreeParseException {
        return parseOr();
    }

    private Expr parseOr() throws DecTreeParseException {
        Expr arg1 = parseAnd();
        int token = nextToken();
        if (token == StreamTokenizer.TT_EOF) {
            return arg1;
        }
        if (token != StreamTokenizer.TT_WORD || !tokenizer.sval.equals("or")) {
            pushBackToken();
            return arg1;
        }
        Expr arg2 = parseOr();
        return new DecTreeDoc.OrExpr(arg1, arg2);
    }

    private Expr parseAnd() throws DecTreeParseException {
        Expr arg1 = parseNot();
        int token = nextToken();
        if (token == StreamTokenizer.TT_EOF) {
            return arg1;
        }
        if (token != StreamTokenizer.TT_WORD || !tokenizer.sval.equals("and")) {
            pushBackToken();
            return arg1;
        }
        Expr arg2 = parseAnd();
        return new DecTreeDoc.AndExpr(arg1, arg2);
    }

    private Expr parseNot() throws DecTreeParseException {
        int token = nextToken();
        if (token == StreamTokenizer.TT_WORD && tokenizer.sval.equals("not")) {
            return new NotExpr(parseNot());
        }
        if (token == '(') {
            Expr expr = parse();
            token = nextToken();
            if (token != ')') {
                throw new DecTreeParseException("missing ')' in condition");
            }
            return expr;
        }
        pushBackToken();
        return parseComp();
    }


    private Expr parseComp() throws DecTreeParseException {
        int token = nextToken();
        if (token != StreamTokenizer.TT_WORD) {
            pushBackToken();
            return null;
        }
        String varName = tokenizer.sval;
        Variable variable = variables.inputs.get(varName);
        if (variable == null) {
            variable = variables.derived.get(varName);
            if (variable == null) {
                pushBackToken();
                return null;
            }
        }
        token = nextToken();
        if (token != StreamTokenizer.TT_WORD || !tokenizer.sval.equals("is")) {
            pushBackToken();
            return null;
        }
        token = nextToken();
        if (token != StreamTokenizer.TT_WORD) {
            throw new DecTreeParseException("property name or \"not\" expected after \"is\"");
        }
        boolean inv = false;
        if (tokenizer.sval.equals("not")) {
            token = nextToken();
            if (token != StreamTokenizer.TT_WORD) {
                throw new DecTreeParseException("property name expected after \"is not\"");
            }
            inv = true;
        }
        String propertyName = tokenizer.sval;
        Property property = variable.type.properties.get(propertyName);
        if (property == null) {
            throw new DecTreeParseException(String.format("\"%s\" is not a property of type \"%s\" of input \"%s\"",
                    propertyName, variable.type.name, varName));
        }
        CompExpr compExpr = new CompExpr(variable, property);
        return inv ? new NotExpr(compExpr) : compExpr;
    }

    private int nextToken() {
        try {
            return this.tokenizer.nextToken();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void pushBackToken() {
        this.tokenizer.pushBack();
    }
}
