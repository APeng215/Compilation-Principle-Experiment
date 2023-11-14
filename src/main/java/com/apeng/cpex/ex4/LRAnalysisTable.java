package com.apeng.cpex.ex4;

import com.apeng.cpex.util.Pair;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class LRAnalysisTable {
    private int size;
    private SortedSet<String> terSymbols;
    private SortedSet<String> nonTerSymbols;
    private List<List<Pair<String, Integer>>> action;
    private List<List<String>> go2;
    public LRAnalysisTable(Set<String> terSymbols, Set<String> nonTerSymbols) {}
}
