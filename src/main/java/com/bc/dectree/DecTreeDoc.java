package com.bc.dectree;

import com.bc.dectree.impl.DecTreeDocParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Data structure representing a decision tree.
 */
public class DecTreeDoc {

    public static final String VERSION = "1.0";

    public final String name;
    public final String version;
    public final Map<String, Type> types;
    public final Map<String, Variable> inputs;
    public final Map<String, DerivedVariable> derived;
    public final Map<String, Variable> outputs;
    public final List<Statement> rules;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Map options;

    public DecTreeDoc(String name,
                      String version,
                      Map options,
                      Map<String, Type> types,
                      Map<String, Variable> inputs,
                      Map<String, DerivedVariable> derived,
                      Map<String, Variable> outputs,
                      List<Statement> rules) {
        assert name != null;
        assert version != null;
        assert types != null;
        assert inputs != null;
        assert derived != null;
        assert outputs != null;
        assert rules != null;
        this.name = name;
        this.version = version;
        this.options = options;
        this.types = types;
        this.inputs = inputs;
        this.derived = derived;
        this.outputs = outputs;
        this.rules = rules;
    }

    /**
     * Loads a decision tree document from a YAML file.
     *
     * @param yamlFile The YAML file.
     * @throws IOException           if an I/O error occurs during code generation.
     * @throws DecTreeParseException if a parse error occurs while interpreting {@code yamlFile}.
     */
    public static DecTreeDoc parse(File yamlFile) throws IOException, DecTreeParseException {
        return DecTreeDocParser.parseDoc(yamlFile);
    }

    /**
     * An expression: A or B, A and B, not A, x is P
     */
    public interface Expr {
        String genCode();
    }

    /**
     * A statement: IfElse (comprising one If, any ElseIf, optional Else), Assigmment, or Block.
     */
    public interface Statement {
        List<String> genCode(Context ctx);
    }

    /**
     * "Truth" context used when generating code from statements.
     */
    public interface Context {
        /**
         * @return name of current "truth" variable.
         */
        String getCurrent();

        /**
         * @return name of last "truth" variable.
         */
        String getLast();

        /**
         * Push a new "truth" variable.
         */
        void push();

        /**
         * Pop current "truth" variable.
         */
        void pop();
    }

    public static class Type {
        public final String name;
        public final Map<String, Property> properties;

        public Type(String name, Map<String, Property> properties) {
            assert name != null;
            assert properties != null;
            this.name = name;
            this.properties = properties;
        }
    }

    public static class Variable {
        public final String name;
        public final Type type;

        public Variable(String name, Type type) {
            assert name != null;
            assert type != null;
            this.name = name;
            this.type = type;
        }
    }

    public static class DerivedVariable extends Variable {
        public final String expression;

        public DerivedVariable(String name, Type type, String expression) {
            super(name, type);
            assert expression != null;
            this.expression = expression;
        }
    }

    public static class MembershipFunction {
        public static final String RETURN_TRUE = "return 1.0;";
        public static final String RETURN_FALSE = "return 0.0;";

        private final Map<String, Object> params;
        private final String codeTemplate;

        public MembershipFunction(Map<String, Object> params, String codeTemplate) {
            this.params = params;
            this.codeTemplate = codeTemplate;
        }

        public boolean isTrue() {
            return RETURN_TRUE.equals(codeTemplate);
        }

        public boolean isFalse() {
            return RETURN_FALSE.equals(codeTemplate);
        }

        public List<String> genCode() {
            String body = codeTemplate;
            for (String paramName : params.keySet()) {
                body = body.replaceAll(String.format("\\$\\{%s\\}", paramName), String.valueOf(params.get(paramName)));
            }
            return Arrays.stream(body.split("\n")).collect(Collectors.toList());
        }
    }

    public static class Property {
        public final String name;
        public final String code;
        public final MembershipFunction membershipFunction;

        public Property(String name, String code, MembershipFunction membershipFunction) {
            assert name != null;
            assert code != null;
            assert membershipFunction != null;
            this.name = name;
            this.code = code;
            this.membershipFunction = membershipFunction;
        }

        public boolean isTrue() {
            return membershipFunction.isTrue();
        }

        public boolean isFalse() {
            return membershipFunction.isFalse();
        }
    }

    public static class Assignment implements Statement {
        final Variable variable;
        final Property property;

        public Assignment(Variable variable, Property property) {
            assert variable != null;
            assert property != null;
            this.variable = variable;
            this.property = property;
        }

        @Override
        public List<String> genCode(Context ctx) {
            List<String> lines = new ArrayList<>();
            if (property.isTrue()) {
                lines.add(String.format("%s = max(%s, %s);", variable.name, variable.name, ctx.getCurrent()));
            } else if (property.isFalse()) {
                lines.add(String.format("%s = max(%s, 1.0 - %s);", variable.name, variable.name, ctx.getCurrent()));
            } else {
                assert false;
            }
            return lines;
        }
    }

    public static class Block implements Statement {
        final List<Statement> statements;

        public Block(List<Statement> statements) {
            assert statements != null;
            this.statements = statements;
        }

        @Override
        public List<String> genCode(Context ctx) {
            List<String> lines = new ArrayList<>();
            for (Statement statement : statements) {
                lines.addAll(statement.genCode(ctx));
            }
            return lines;
        }
    }

    public static class If implements Statement {
        final Expr condition;
        final Statement body;

        public If(Expr condition, Statement body) {
            assert condition != null;
            assert body != null;
            this.condition = condition;
            this.body = body;
        }

        @Override
        public List<String> genCode(Context ctx) {
            List<String> lines = new ArrayList<>();
            lines.add(String.format("double %s = min(%s, %s);", ctx.getCurrent(), ctx.getLast(), condition.genCode()));
            lines.addAll(body.genCode(ctx));
            return lines;
        }
    }

    public static class ElseIf extends If {
        public ElseIf(Expr condition, Statement body) {
            super(condition, body);
        }

        @Override
        public List<String> genCode(Context ctx) {
            List<String> lines = new ArrayList<>();
            lines.add(String.format("%s = max(1.0 - %s, %s);", ctx.getCurrent(), ctx.getCurrent(), condition.genCode()));
            lines.addAll(body.genCode(ctx));
            return lines;
        }
    }

    public static class Else implements Statement {
        final Statement body;

        public Else(Statement body) {
            assert body != null;
            this.body = body;
        }

        @Override
        public List<String> genCode(Context ctx) {
            List<String> lines = new ArrayList<>();
            lines.add(String.format("%s = 1.0 - %s;", ctx.getCurrent(), ctx.getCurrent()));
            lines.addAll(body.genCode(ctx));
            return lines;
        }
    }

    public static class IfElse implements Statement {
        final List<If> ifStatements;
        final Else elseStatement;

        public IfElse(List<If> ifStatements, Else elseStatement) {
            assert ifStatements != null;
            this.ifStatements = ifStatements;
            this.elseStatement = elseStatement;
        }

        @Override
        public List<String> genCode(Context ctx) {
            ctx.push();
            List<String> lines = new ArrayList<>();
            lines.add("");
            for (If ifStatement : ifStatements) {
                lines.addAll(ifStatement.genCode(ctx));
            }
            if (elseStatement != null) {
                lines.addAll(elseStatement.genCode(ctx));
            }
            ctx.pop();
            return lines;
        }
    }

    public static class CompExpr implements Expr {
        public final Variable variable;
        public final Property property;

        public CompExpr(Variable variable, Property property) {
            assert variable != null;
            assert property != null;
            this.variable = variable;
            this.property = property;
        }

        public String genCode() {
            return String.format("%s_%s(%s)", variable.type.name, property.name, variable.name);
        }
    }

    public static class NotExpr implements Expr {
        public final Expr arg;

        public NotExpr(Expr arg) {
            assert arg != null;
            this.arg = arg;
        }

        public String genCode() {
            return String.format("1.0 - %s", arg.genCode());
        }
    }

    public static class AndExpr implements Expr {
        public final Expr arg1;
        public final Expr arg2;

        public AndExpr(Expr arg1, Expr arg2) {
            assert arg1 != null;
            assert arg2 != null;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        public String genCode() {
            return String.format("min(%s, %s)", arg1.genCode(), arg2.genCode());
        }
    }

    public static class OrExpr implements Expr {
        public final Expr arg1;
        public final Expr arg2;

        public OrExpr(Expr arg1, Expr arg2) {
            assert arg1 != null;
            assert arg2 != null;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        public String genCode() {
            return String.format("max(%s, %s)", arg1.genCode(), arg2.genCode());
        }
    }

}