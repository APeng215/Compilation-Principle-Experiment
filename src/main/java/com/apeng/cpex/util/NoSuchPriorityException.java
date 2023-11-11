package com.apeng.cpex.util;

public class NoSuchPriorityException extends RuntimeException {
    public NoSuchPriorityException(String rowSymbol, String colSymbol) {
        super(String.format("RowSymbol: '%s' ColSymbol: '%s'", rowSymbol, colSymbol));
    }
}
