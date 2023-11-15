package com.apeng.cpex.ex4;

import com.apeng.cpex.util.Pair;

import java.util.*;

public class LRAnalysisTable {
    private int size;
    List<List<Project>> C;
    private List<String> terSymbols;
    private List<String> nonTerSymbols;
    private List<Pair<String, String>> formulas;
    private List<List<Pair<String, Integer>>> action;
    private List<List<Integer>> go2;


    public LRAnalysisTable(List<List<Project>> C, Set<String> terSymbolSet, Set<String> nonTerSymbolSet, Map<String, Set<String>> formulaSets) {
        this.size = C.size();
        this.C = C;
        this.terSymbols = new ArrayList<>(terSymbolSet);
        this.terSymbols.add("#");
        this.nonTerSymbols = new ArrayList<>(nonTerSymbolSet);
        initFormulas(formulaSets);
        initActionAndGo2();
    }

    public List<String> getTerSymbols() {
        return terSymbols;
    }

    private void initFormulas(Map<String, Set<String>> formulaSets) {
        formulas = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : formulaSets.entrySet()) {
            String left = entry.getKey();
            for (String rights : entry.getValue()) {
                Pair<String, String> formula = new Pair<>(left, rights);
                formulas.add(formula);
            }
        }
    }

    private void initActionAndGo2() {
        action = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Pair<String, Integer>> temp = new ArrayList<>();
            for (int j = 0; j < terSymbols.size(); j++) {
                temp.add(null);
            }
            action.add(temp);
        }

        go2 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Integer> temp = new ArrayList<>();
            for (int j = 0; j < nonTerSymbols.size(); j++) {
                temp.add(null);
            }
            go2.add(temp);
        }
    }

    public void setAction(int state, String terSymbol, String operation, int nextState) {
        this.action.get(state).set(terSymbols.indexOf(terSymbol), new Pair<>(operation, nextState));
    }

    public Pair<String, Integer> getAction(int state, String terSymbol) {
        return this.action.get(state).get(terSymbols.indexOf(terSymbol));
    }

    public void setGo2(int state, String nonTerSymbol, int nextState) {
        this.go2.get(state).set(nonTerSymbols.indexOf(nonTerSymbol), nextState);
    }

    public int getGo2(int state, String nonTerSymbol) {
        return this.go2.get(state).get(nonTerSymbols.indexOf(nonTerSymbol));
    }

    public List<Pair<String, String>> getFormulas() {
        return formulas;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\t");
        for (String terSymbol : terSymbols) {
            builder.append(terSymbol + "\t");
        }
        for (String nonTerSymbol : nonTerSymbols) {
            if (!nonTerSymbol.equals("W")){
                builder.append(nonTerSymbol + "\t");
            }
        }
        builder.append("\n");

        for (int i = 0; i < size; i++) {
            builder.append(i + "\t");
            for (Pair<String, Integer> operate : action.get(i)) {
                // TODO Record throws Exception when trying to get its null property
                if (operate == null) {
                    builder.append("\t");
                    continue;
                }
                if (operate.first().equals("acc")) {
                    builder.append("acc\t");
                    continue;
                }
                builder.append(operate.first()).append(operate.second()).append("\t");

            }
            for (Integer dist : go2.get(i)) {
                builder.append(dist == null ? "" : dist).append("\t");
            }
            if (i != size - 1){
                builder.append("\n");
            }
        }

        return builder.toString();
    }
}
