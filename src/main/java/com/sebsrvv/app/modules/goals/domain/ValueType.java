package com.sebsrvv.app.modules.goals.domain;

public enum ValueType {
    QUANTITATIVE, BOOLEAN;

    public static boolean isValid(String s) {
        if (s == null) return false;
        try { valueOf(s); return true; }
        catch (IllegalArgumentException ex) { return false; }
    }
}
