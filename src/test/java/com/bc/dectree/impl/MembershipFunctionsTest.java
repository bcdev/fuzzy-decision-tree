package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.stream.Collectors;

import static com.bc.dectree.impl.MembershipFunctions.*;
import static org.junit.Assert.assertTrue;

public class MembershipFunctionsTest {

    @Test
    public void test_true() {
        double[][] points = {
                {-0.25, 0.00},
                {+0.00, 0.00},
                {+0.25, 0.25},
                {+0.50, 0.50},
                {+0.75, 0.75},
                {+1.00, 1.00},
                {+1.25, 1.00}};
        runFuncTest(TRUE(Utilities.map0()), points);
    }

    @Test
    public void test_false() {
        double[][] points = {
                {-0.25, 1.00},
                {+0.00, 1.00},
                {+0.25, 0.75},
                {+0.50, 0.50},
                {+0.75, 0.25},
                {+1.00, 0.00},
                {+1.25, 0.00}};
        runFuncTest(FALSE(Utilities.map0()), points);
    }

    @Test
    public void test_eq() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.25, 0.0},
                {+0.50, 1.0},
                {+0.75, 0.0},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(EQ(Utilities.map1("x0", 0.5)), points);
    }

    @Test
    public void test_eq_dx() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.30, 0.5},
                {+0.50, 1.0},
                {+0.70, 0.5},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(EQ(Utilities.map2("x0", 0.5, "dx", 0.4)), points);
    }

    @Test
    public void test_ne() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.25, 1.0},
                {+0.50, 0.0},
                {+0.75, 1.0},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(NE(Utilities.map1("x0", 0.5)), points);
    }

    @Test
    public void test_ne_dx() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.30, 0.5},
                {+0.50, 0.0},
                {+0.70, 0.5},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(NE(Utilities.map2("x0", 0.5, "dx", 0.4)), points);
    }

    @Test
    public void test_gt() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.25, 0.0},
                {+0.50, 0.0},
                {+0.75, 1.0},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(GT(Utilities.map1("x0", 0.5)), points);
    }

    @Test
    public void test_ge() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.25, 0.0},
                {+0.50, 1.0},
                {+0.75, 1.0},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(GE(Utilities.map1("x0", 0.5)), points);
    }

    @Test
    public void test_gt_ge_dx() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.30, 0.25},
                {+0.50, 0.5},
                {+0.70, 0.75},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(GT(Utilities.map2("x0", 0.5, "dx", 0.4)), points);
        runFuncTest(GE(Utilities.map2("x0", 0.5, "dx", 0.4)), points);
    }

    @Test
    public void test_lt() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.25, 1.0},
                {+0.50, 0.0},
                {+0.75, 0.0},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(LT(Utilities.map1("x0", 0.5)), points);
    }

    @Test
    public void test_le() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.25, 1.0},
                {+0.50, 1.0},
                {+0.75, 0.0},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(LE(Utilities.map1("x0", 0.5)), points);
    }

    @Test
    public void test_lt_le_dx() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.30, 0.75},
                {+0.50, 0.5},
                {+0.70, 0.25},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(LT(Utilities.map2("x0", 0.5, "dx", 0.4)), points);
        runFuncTest(LE(Utilities.map2("x0", 0.5, "dx", 0.4)), points);
    }

    @Test
    public void test_ramp() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.25, 0.25},
                {+0.50, 0.5},
                {+0.75, 0.75},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(RAMP(Utilities.map0()), points);
    }

    @Test
    public void test_inv_ramp() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.25, 0.75},
                {+0.50, 0.5},
                {+0.75, 0.25},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(INV_RAMP(Utilities.map0()), points);
    }

    @Test
    public void test_triangular() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.25, 0.5},
                {+0.50, 1.0},
                {+0.75, 0.5},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(TRIANGULAR(Utilities.map0()), points);
    }

    @Test
    public void test_inv_triangular() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.25, 0.5},
                {+0.50, 0.0},
                {+0.75, 0.5},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(INV_TRIANGULAR(Utilities.map0()), points);
    }

    @Test
    public void test_trapezoid() {
        double[][] points = {
                {-0.25, 0.0},
                {+0.00, 0.0},
                {+0.25, 0.75},
                {+0.35, 1.0},
                {+0.50, 1.0},
                {+0.65, 1.0},
                {+0.75, 0.75},
                {+1.00, 0.0},
                {+1.25, 0.0}};
        runFuncTest(TRAPEZOID(Utilities.map0()), points);
    }

    @Test
    public void test_inv_trapezoid() {
        double[][] points = {
                {-0.25, 1.0},
                {+0.00, 1.0},
                {+0.25, 0.25},
                {+0.35, 0.0},
                {+0.50, 0.0},
                {+0.65, 0.0},
                {+0.75, 0.25},
                {+1.00, 1.0},
                {+1.25, 1.0}};
        runFuncTest(INV_TRAPEZOID(Utilities.map0()), points);
    }

    private void runFuncTest(DecTreeDoc.MembershipFunction r, double[][] points) {
        String body = String.join("\n", r.genCode().stream().map(s -> "        " + s).collect(Collectors.toList()));;
        String source = "" +
                "package com.bc.dectree;\n" +
                "\n" +
                "public class Func {\n" +
                "    public static double eval(double x) {\n" +
                body + "\n" +
                "    }\n" +
                "}\n";

        try {

            // Save source in .java file.
            File root = new File("./temp/java").getAbsoluteFile();
            File sourceFile = new File(root, "com/bc/dectree/Func.java");
            //noinspection ResultOfMethodCallIgnored
            sourceFile.getParentFile().mkdirs();
            Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

            // Compile source file.
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, sourceFile.getPath());

            // Load and instantiate compiled class.
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
            Class<?> cls = Class.forName("com.bc.dectree.Func", true, classLoader);
            Method evalMethod = cls.getMethod("eval", Double.TYPE);
            for (int i = 0; i < points.length; i++) {
                double[] point = points[i];
                Object result = evalMethod.invoke(null, point[0]);
                assertTrue(MessageFormat.format("at index {0}, x = {1}: expected y = {2}, actual y = {3}",
                        i, point[0], point[1], result), Math.abs(point[1] - (Double) result) < 1e-6);
            }
        } catch (IOException
                | ClassNotFoundException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }
}