package com.apeng.cpex.ex3;

import com.apeng.cpex.util.NoSuchPriorityException;

import java.sql.Struct;
import java.util.List;

public class PriorityTable {
    private String[][] table;
    private final List<String> terSymbols;
    public PriorityTable(List<String> terSymbols) {
        table = new String[terSymbols.size()][terSymbols.size()];
        this.terSymbols = terSymbols;
    }
    public void print() {
        System.out.print(this);
    }
    public void set(String row, String column, String value) {
        table[terSymbols.indexOf(row)][terSymbols.indexOf(column)] = value;
    }
    public void set(int row, int column, String value) {
        table[row][column] = value;
    }
    public void set(char row, char column, String value) {
        set(String.valueOf(row), String.valueOf(column), value);
    }
    public String get(String row, String column) {
        String result = table[terSymbols.indexOf(row)][terSymbols.indexOf(column)];
        if (result != null) {
            return result;
        }
        throw new NoSuchPriorityException(row, column);
    }
    public String get(int row, int column) {
        return table[row][column];
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Priority Table:\n").append(" ").append(" ");
        for (String terSymbol : terSymbols) {
            stringBuilder.append(terSymbol).append(" ");
        }
        stringBuilder.append("\n");
        for (int i = 0; i < table.length; i++) {
            stringBuilder.append(terSymbols.get(i)).append(" ");
            for (String s : table[i]) {
                stringBuilder.append(s == null ? " " : s).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString().trim();
    }
}
