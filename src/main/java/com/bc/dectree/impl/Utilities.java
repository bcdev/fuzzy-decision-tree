package com.bc.dectree.impl;

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

    public static String getPackageName(String className) {
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
}
