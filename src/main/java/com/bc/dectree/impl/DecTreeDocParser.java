package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import com.bc.dectree.DecTreeDoc.*;
import com.bc.dectree.DecTreeParseException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.bc.dectree.impl.Utilities.POSITIONAL_PARAM_PREFIX;


public class DecTreeDocParser {

    private static Pattern IF_COND = Pattern.compile("if\\s*(?<cond>.+)");
    private static Pattern ELSE_IF_COND = Pattern.compile("else\\s*if\\s*(?<cond>.+)");

    private String docName;
    private Map rootMap;
    private Stack<Object> elementStack;

    DecTreeDocParser(String docName, Map rootMap) {
        this.docName = docName;
        this.rootMap = rootMap;
        this.elementStack = new Stack<>();
    }

    public static DecTreeDoc parseDoc(File yamlFile) throws IOException, DecTreeParseException {
        try (Reader reader = new FileReader(yamlFile)) {
            return parseDoc(reader, yamlFile.getPath());
        }
    }

    private static DecTreeDoc parseDoc(Reader yamlReader, String docName) throws DecTreeParseException {
        Yaml yaml = new Yaml();
        Object obj;
        try {
            obj = yaml.load(yamlReader);
        } catch (org.yaml.snakeyaml.parser.ParserException e) {
            throw new DecTreeParseException(String.format("%s: invalid dectree-YAML document:\n%s", docName, e.getMessage()), e);
        }
        if (!(obj instanceof Map)) {
            throw new DecTreeParseException(String.format("%s: invalid dectree-YAML document", docName));
        }
        Map map = (Map) obj;
        DecTreeDocParser parser = new DecTreeDocParser(docName, map);
        return parser.parse();
    }

    private static boolean isClassName(String name) {
        if (!name.contains(".")) {
            return isJavaIdentifier(name);
        }
        String[] parts = name.split(".");
        for (String part : parts) {
            if (!isJavaIdentifier(part)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isJavaIdentifier(String name) {
        if (name.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private DecTreeDoc parse() throws DecTreeParseException {
        @SuppressWarnings("unchecked") String name = toClassName(rootMap.get("name"), "name");
        @SuppressWarnings("unchecked") String version = toString(rootMap.getOrDefault("version", DecTreeDoc.VERSION), "version");
        Map options = getMapDocElement("options", false);
        Map<String, Type> types = parseTypes("types");
        Map<String, Variable> inputs = parseVariables("inputs", "input variable", types);
        Map<String, Variable> outputs = parseVariables("outputs", "output variable", types);
        Map<String, DerivedVariable> derived = parseDerivedVariables("derived", "derived variable", types);
        List<Statement> statements = parseStatements("rules", "rule", new Variables(inputs, derived, outputs));
        return new DecTreeDoc(name, version, options, types, inputs, derived, outputs, statements);
    }

    @SuppressWarnings("SameParameterValue")
    private List<Statement> parseStatements(String elementName, String tag,
                                            Variables variables) throws DecTreeParseException {
        pushElement(elementName);
        List rules = getListDocElement(elementName);
        List<Statement> statements = new ArrayList<>();
        assert rules != null;
        for (int i = 0; i < rules.size(); i++) {
            pushElement(i);
            Object rule = rules.get(i);
            statements.add(parseStatement(toMap(rule, tag), variables));
            popElement();
        }
        popElement();
        return statements;
    }

    private Statement parseStatement(Map map, Variables variables) throws DecTreeParseException {
        List<Statement> statements = new ArrayList<>();
        List<If> ifStatements = null;
        Else elseStatement = null;
        int numStatements = map.size();
        int index = 0;
        for (Object stmtObj : map.keySet()) {
            pushElement(stmtObj);
            Object bodyObj = map.get(stmtObj);

            String stmt = toString(stmtObj, "if-statement or assignment").trim();
            if (stmt.equals("else")) {
                if (ifStatements == null) {
                    throw newParseException("\"else\" without matching \"if\"");
                }
                if (elseStatement != null) {
                    throw newParseException("\"else\" cannot occur twice");
                }
                if (index != numStatements - 1) {
                    throw newParseException("\"else\" must be last statement");
                }
                elseStatement = new Else(parseStatement(toMap(bodyObj, "\"else\"-body"), variables),
                                         formatCode(stmt + ":"));
            } else {
                Matcher matcher = ELSE_IF_COND.matcher(stmt);
                if (matcher.matches()) {
                    if (ifStatements == null) {
                        throw newParseException("\"else if\" without matching \"if\"");
                    }
                    String cond = matcher.group("cond");
                    ifStatements.add(new ElseIf(parseExpr(cond, variables), parseStatement(toMap(bodyObj, "\"else if\"-body"), variables),
                                                formatCode(stmt + ":")));
                } else {
                    matcher = IF_COND.matcher(stmt);
                    if (matcher.matches()) {
                        if (ifStatements != null) {
                            statements.add(new IfElse(ifStatements, elseStatement));
                        }
                        ifStatements = new ArrayList<>();
                        elseStatement = null;
                        String cond = matcher.group("cond");
                        ifStatements.add(new If(parseExpr(cond, variables), parseStatement(toMap(bodyObj, "\"if\"-body"), variables),
                                                formatCode(stmt + ":")));
                    } else if (isJavaIdentifier(stmt)) {
                        if (!variables.outputs.containsKey(stmt)) {
                            throw newParseException(String.format("output expected, but found \"%s\"", stmt));
                        }
                        if (ifStatements != null) {
                            statements.add(new IfElse(ifStatements, elseStatement));
                            ifStatements = null;
                            elseStatement = null;
                        }
                        Variable variable = variables.outputs.get(stmt);
                        // TODO
                        //if (variable.type != Type.BOOLEAN) {
                        //    throw newParseException(String.format("output must have type \"%s\"", Type.BOOLEAN.name));
                        //}
                        boolean propValue;
                        if (bodyObj instanceof Boolean) {
                            propValue = (Boolean) bodyObj;
                        } else if (bodyObj instanceof Number) {
                            double number = ((Number) bodyObj).doubleValue();
                            if (number == 1.0) {
                                propValue = true;
                            } else if (number == 0.0) {
                                propValue = false;
                            } else {
                                throw newParseException("illegal output value, must be either 0 or 1");
                            }
                        } else {
                            throw newParseException("illegal output value, must be either TRUE or FALSE");
                        }
                        statements.add(new Assignment(variable, propValue,
                                                      formatCode(stmt + ": " + bodyObj)));
                    } else {
                        throw newParseException("\"if\", \"else if\", or \"else\" expected");
                    }
                }
            }
            popElement();
            index++;
        }
        if (ifStatements != null) {
            statements.add(new IfElse(ifStatements, elseStatement));
        }
        if (statements.size() == 1) {
            return statements.get(0);
        }
        return new Block(statements);
    }

    private Expr parseExpr(String cond, Variables variables) throws DecTreeParseException {
        try {
            return DecTreeExprParser.parseExpr(cond, variables);
        } catch (DecTreeParseException e) {
            throw newParseException(e.getMessage());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Map<String, Type> parseTypes(String elementName) throws DecTreeParseException {
        pushElement(elementName);
        Map map = getMapDocElement(elementName, true);
        assert map != null;
        Map<String, Type> types = new LinkedHashMap<>();
        types.put(Type.NUMBER.name, Type.NUMBER);
        types.put(Type.BOOLEAN.name, Type.BOOLEAN);
        for (Object nameObj : map.keySet()) {
            pushElement(nameObj);
            String typeName = toName(nameObj, "type name");
            if (types.containsKey(typeName)) {
                throw newParseException("type already defined");
            }
            types.put(typeName, parseType(typeName, map.get(nameObj)));
            popElement();
        }
        popElement();
        return types;
    }

    private Type parseType(String typeName, Object typeObj) throws DecTreeParseException {
        Map map = toMap(typeObj, "type definition");
        Map<String, Property> properties = new LinkedHashMap<>();
        for (Object nameObj : map.keySet()) {
            pushElement(nameObj);
            String propertyName = toName(nameObj, "property name");
            String propertyCode = toString(map.get(nameObj), "property value");
            properties.put(propertyName, parseProperty(propertyName, propertyCode));
            popElement();
        }
        return new Type(typeName, properties);
    }

    private Property parseProperty(String propertyName, String propertyCode) throws DecTreeParseException {
        String functionName;
        Map<String, Double> functionParameters;
        int i1 = propertyCode.indexOf('(');
        if (i1 >= 0) {
            int i2 = propertyCode.indexOf(')', i1);
            if (i2 >= 0) {
                functionName = propertyCode.substring(0, i1).trim();
                functionParameters = parseFunctionParameters(propertyCode.substring(i1 + 1, i2).trim());
            } else {
                throw newParseException("missing ')'");
            }
        } else {
            functionName = propertyCode.trim();
            functionParameters = Collections.emptyMap();
        }
        if (!isJavaIdentifier(functionName)) {
            throw newParseException(String.format("invalid property name: \"%s\"", propertyName));
        }

        DecTreeDoc.MembershipFunction functionBody;
        try {
            Method method = MembershipFunctions.class.getDeclaredMethod(functionName.toUpperCase(), Map.class);
            functionBody = (DecTreeDoc.MembershipFunction) method.invoke(null, functionParameters);
        } catch (NoSuchMethodException e) {
            throw newParseException(String.format("unknown function \"%s\"", functionName));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }

        return new Property(propertyName, functionBody, propertyCode);
    }

    Map<String, Double> parseFunctionParameters(String functionArgsCode) throws DecTreeParseException {
        Map<String, Double> functionParameters = new LinkedHashMap<>();
        String[] nameValuePairs = functionArgsCode.split(",");
        int paramIndex = 0;
        for (String nameValuePair : nameValuePairs) {
            nameValuePair = nameValuePair.trim();
            if (nameValuePair.isEmpty()) {
                continue;
            }
            String[] nameValue = nameValuePair.split("=");
            if (nameValue.length == 1) {
                String name = POSITIONAL_PARAM_PREFIX + paramIndex;
                String value = nameValue[0].trim();
                try {
                    functionParameters.put(name, Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    throw newParseException(String.format("invalid value for parameter at position %s: \"%s\"", paramIndex + 1, value));
                }
            } else if (nameValue.length == 2) {
                String name = nameValue[0].trim();
                String value = nameValue[1].trim();
                if (name.isEmpty()) {
                    throw newParseException("missing parameter name");
                } else if (!isJavaIdentifier(name)) {
                    throw newParseException(String.format("invalid parameter name: \"%s\"", name));
                }
                try {
                    functionParameters.put(name, Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    throw newParseException(String.format("invalid value for parameter \"%s\": \"%s\"", name, value));
                }
            } else {
                throw newParseException(String.format("invalid parameter \"%s\"", nameValuePair));
            }
            paramIndex++;
        }
        return functionParameters;
    }

    private Map<String, Variable> parseVariables(String elementName, String tag, Map<String, Type> types) throws DecTreeParseException {
        pushElement(elementName);
        Map map = getMapDocElement(elementName, true);
        assert map != null;
        Map<String, Variable> variables = new LinkedHashMap<>();
        for (Object nameObj : map.keySet()) {
            pushElement(nameObj);
            String variableName = toName(nameObj, tag);
            if (variables.containsKey(variableName)) {
                throw newParseException("%s is already defined");
            }
            Type type = toTypeDef(map.get(variableName), types, tag);
            variables.put(variableName, new Variable(variableName, type));
            popElement();
        }
        popElement();
        return variables;
    }

    @SuppressWarnings("SameParameterValue")
    private Map<String, DerivedVariable> parseDerivedVariables(String elementName, String tag, Map<String, Type> types) throws DecTreeParseException {
        pushElement(elementName);
        Map map = getMapDocElement(elementName, false);
        Map<String, DerivedVariable> variables = new LinkedHashMap<>();
        if (map != null) {
            for (Object assignmentObj : map.keySet()) {
                String assignment = toString(assignmentObj, tag);
                String[] split = assignment.split("=");
                if (split.length != 2) {
                    throw newParseException(String.format("assignment expected, but found \"%s\"", assignment));
                }
                String variableName = split[0].trim();
                String expression = split[1].trim();
                if (variables.containsKey(variableName)) {
                    throw newParseException(String.format("variable \"%s\" is already defined", variableName));
                }
                Type type = toTypeDef(map.get(assignmentObj), types, tag);
                variables.put(variableName, new DerivedVariable(variableName, type, expression));
            }
        }
        popElement();
        return variables;
    }

    private Map getMapDocElement(String elementName, boolean notNull) throws DecTreeParseException {
        Object value = rootMap.get(elementName);
        if (value == null) {
            if (notNull) {
                throw newParseException(String.format("missing element \"%s\"", elementName));
            }
            return null;
        }
        if (!(value instanceof Map)) {
            throw newParseException(String.format("element \"%s\" must be a mapping", elementName));
        }
        return (Map) value;
    }

    private List getListDocElement(String elementName) throws DecTreeParseException {
        Object obj = rootMap.get(elementName);
        if (obj == null) {
            throw newParseException(String.format("missing element \"%s\"", elementName));
        }
        if (!(obj instanceof List)) {
            throw newParseException(String.format("element \"%s\" must be a list", elementName));
        }
        return (List) obj;
    }

    private Map toMap(Object value, String tag) throws DecTreeParseException {
        if (!(value instanceof Map)) {
            throw newParseException(String.format("%s must be map", tag));
        }
        return (Map) value;
    }

    private Type toTypeDef(Object value, Map<String, Type> types, String tag) throws DecTreeParseException {
        String typeName = toName(value, tag);
        Type type = types.get(typeName);
        if (type == null) {
            throw newParseException(String.format("unknown type \"%s\"", typeName));
        }
        return type;
    }

    @SuppressWarnings("SameParameterValue")
    private String toClassName(Object value, String tag) throws DecTreeParseException {
        String s = toString(value, tag);
        if (!isClassName(s)) {
            throw newParseException(String.format("invalid %s", tag));
        }
        return (String) value;
    }

    private String toName(Object value, String tag) throws DecTreeParseException {
        String s = toString(value, tag);
        if (!isJavaIdentifier(s)) {
            throw newParseException(String.format("invalid %s", tag));
        }
        return (String) value;
    }

    private String toString(Object value, String tag) throws DecTreeParseException {
        if (value == null) {
            throw newParseException(String.format("invalid %s, must be a character string, was null", tag));
        }
        if (!(value instanceof String)) {
            throw newParseException(String.format("invalid %s, must be a character string (was %s)", tag, value.getClass().getSimpleName()));
        }
        return (String) value;
    }

    private DecTreeParseException newParseException(String message) {
        String path = Arrays.stream(elementStack.toArray()).map(String::valueOf).collect(Collectors.joining("/"));
        String msg;
        if (path.isEmpty()) {
            msg = String.format("%s: %s", docName, message);
        } else {
            msg = String.format("%s: element \"%s\": %s", docName, path, message);
        }
        return new DecTreeParseException(msg);
    }

    void pushElement(Object elementName) {
        elementStack.push(elementName);
    }

    void popElement() {
        elementStack.pop();
    }

    private String formatCode(String code) {
        StringBuilder indentedCode = new StringBuilder();
        for (int i = 0; i < elementStack.size() - 3; i++) {
            indentedCode.append("    ");
        }
        indentedCode.append(code);
        return indentedCode.toString();
    }

    static class Variables {
        final Map<String, Variable> inputs;
        final Map<String, DerivedVariable> derived;
        final Map<String, Variable> outputs;

        Variables(Map<String, Variable> inputs, Map<String, DerivedVariable> derived, Map<String, Variable> outputs) {
            assert inputs != null;
            assert derived != null;
            assert outputs != null;
            this.inputs = inputs;
            this.derived = derived;
            this.outputs = outputs;
        }
    }
}
