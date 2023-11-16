package com.apeng.cpex.ex2;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {
    private int size = 0;
    private List<String> stacks = new ArrayList<>();
    private List<String> inputs = new ArrayList<>();
    private List<String> usedFormulas = new ArrayList<>();


    public void add(String stack, String input, String usedFormula) {
        stacks.add(stack);
        inputs.add(input);
        usedFormulas.add(usedFormula);
        size++;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s      %s          %s  %s\n", "步骤", "符号栈", "输入串", "所用产生式"));
        for (int i = 0; i < size; i++) {
            builder.append(String.format("%-10s%-10s%10s   %-10s\n", i, stacks.get(i), inputs.get(i), usedFormulas.get(i)));
        }
        return builder.toString().trim();
    }
}
