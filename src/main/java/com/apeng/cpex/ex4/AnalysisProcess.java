package com.apeng.cpex.ex4;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnalysisProcess {
    private int stepNum = 0;
    private final List<String> stateStack = new ArrayList<>();
    private final List<String> symbolStack = new ArrayList<>();
    private final List<String> unresolvedSentence = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();
    public void addStep(Stack<Integer> stateStack, Stack<String> symbolStack, String unresolvedSentence, String operatorConcatState) {
        this.stateStack.add(stateStack.toString());
        this.symbolStack.add(symbolStack.toString());
        this.unresolvedSentence.add(unresolvedSentence);
        this.actions.add(new Action(stateStack.peek(), unresolvedSentence.substring(0, 1), operatorConcatState));
        this.stepNum++;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stepNum; i++) {
            if (i != stepNum - 1) {
                builder.append("StateStack: ").append(stateStack.get(i)).append("\n");
                builder.append("SymbolStack: ").append(symbolStack.get(i)).append("\n");
                builder.append("UnresolvedSentence: ").append(unresolvedSentence.get(i)).append("\n");
                builder.append("Action: ").append(actions.get(i)).append("\n").append("\n");
            } else {
                builder.append("StateStack: ").append(stateStack.get(i)).append("\n");
                builder.append("SymbolStack: ").append(symbolStack.get(i)).append("\n");
                builder.append("UnresolvedSentence: ").append(unresolvedSentence.get(i)).append("\n");
                builder.append("Action: ").append(actions.get(i)).append("\n");
            }
        }
        return builder.toString();
    }
}
