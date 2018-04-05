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
        assert varStack.size() >= 1;
        return varStack.peek();
    }

    public String getLast() {
        assert varStack.size() >= 2;
        return varStack.get(varStack.size() - 2);
    }

    @Override
    public String getPenultimate() {
        assert varStack.size() >= 3;
        return varStack.get(varStack.size() - 3);
    }

    public void push() {
        id++;
        varStack.push(mkVarName());
    }

    public void pop() {
        assert varStack.size() >= 1;
        varStack.pop();
    }

    public void pop(int n) {
        assert varStack.size() >= n;
        for (int i = 0; i < n; i++) {
            varStack.pop();
        }
    }

    private String mkVarName() {
        return String.format("_t%s", id);
    }
}
