package com.bc.dectree.impl;

import com.bc.dectree.DecTreeDoc.Context;

import java.util.Stack;


public class ContextImpl implements Context {
    private int id;
    private Stack<String> varStack;

    ContextImpl() {
        id = 0;
        varStack = new Stack<>();
        varStack.push(mkVarName());
    }

    public String getCurrent() {
        return varStack.peek();
    }

    public String getLast() {
        return varStack.get(varStack.size() - 2);
    }

    public void push() {
        id++;
        varStack.push(mkVarName());
    }

    public void pop() {
        varStack.pop();
    }

    private String mkVarName() {
        return String.format("_t%s", id);
    }
}
