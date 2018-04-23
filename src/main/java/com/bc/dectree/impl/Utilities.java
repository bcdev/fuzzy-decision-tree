package com.bc.dectree.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@SuppressWarnings("SameParameterValue")
public class Utilities {
    static final Object UNDEFINED = new Object();
    static final String POSITIONAL_PARAM_PREFIX = "param.";

    public static String getSimpleClassName(String className) {
        int endIndex = className.lastIndexOf('.');
        return endIndex == -1 ? className : className.substring(endIndex + 1);
    }

    static String getPackageName(String className) {
        int endIndex = className.lastIndexOf('.');
        return endIndex == -1 ? "" : className.substring(0, endIndex);
    }

    public static Map<String, Object> map0() {
        return Collections.emptyMap();
    }

    public static Map<String, Object> map1(String name1, Object value1) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put(name1, value1);
        return map;
    }

    public static Map<String, Object> map2(String name1, Object value1,
                                           String name2, Object value2) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put(name1, value1);
        map.put(name2, value2);
        return map;
    }

    static Map<String, Object> map3(String name1, Object value1,
                                    String name2, Object value2,
                                    String name3, Object value3) {
        HashMap<String, Object> map = new HashMap<>(3);
        map.put(name1, value1);
        map.put(name2, value2);
        map.put(name3, value3);
        return map;
    }

    static Map<String, Object> map4(String name1, Object value1,
                                    String name2, Object value2,
                                    String name3, Object value3,
                                    String name4, Object value4) {
        HashMap<String, Object> map = new HashMap<>(4);
        map.put(name1, value1);
        map.put(name2, value2);
        map.put(name3, value3);
        map.put(name4, value4);
        return map;
    }

    static Map<String, Object> merge(Map<String, Object> params, Map<String, Object> defaults) {
        for (String paramName : params.keySet()) {
            if (!defaults.containsKey(paramName) && !paramName.startsWith(POSITIONAL_PARAM_PREFIX)) {
                throw new IllegalArgumentException(String.format("illegal function parameter \"%s\"", paramName));
            }
        }
        Map<String, Object> newArgs = new LinkedHashMap<>();
        int paramIndex = 0;
        for (String paramName : defaults.keySet()) {
            Object defaultValue = defaults.get(paramName);
            Object value = params.getOrDefault(paramName, params.getOrDefault(POSITIONAL_PARAM_PREFIX + paramIndex, defaultValue));
            if (value == UNDEFINED) {
                throw new IllegalArgumentException(String.format("missing value for parameter \"%s\"", paramName));
            }
            newArgs.put(paramName, value);
            paramIndex++;
        }
        return newArgs;
    }

    static File getCodeSource(Class cls) {

        ClassLoader loader = cls.getClassLoader();
        if (loader == null) {
            // Try the bootstrap classloader - obtained from the ultimate parent of the System Class Loader.
            loader = ClassLoader.getSystemClassLoader();
            while (loader != null && loader.getParent() != null) {
                loader = loader.getParent();
            }
        }

        if (loader != null) {
            String className = cls.getCanonicalName();
            URL resource = loader.getResource(className.replace(".", "/") + ".class");
            if (resource != null) {
                String url = resource.toString();
                String packageName = cls.getPackage().getName();
                int packageIndex = url.lastIndexOf(packageName.replace('.', '/'));
                if (packageIndex > 0) {
                    url = url.substring(0, packageIndex);
                }
                if (url.startsWith("jar:")) {
                    url = url.substring(4);
                }
                if (url.endsWith("!/")) {
                    url = url.substring(0, url.length() - 2);
                }
                if (url.startsWith("file:")) {
                    try {
                        return new File(new URI(url));
                    } catch (URISyntaxException e) {
                        // pass
                    }
                }
            }
        }

        return null;
    }
}
