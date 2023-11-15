package com.apeng.cpex.ex3;

import com.apeng.cpex.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class OperatorFirstAnalyzer {


    private String startSymbol;//开始符
    private List<String> terSymbolList; // 终结符
    private List<String> nonTerSymbolList; // 非终结符
    private final HashMap<String, Set<String>> formulaSetMap = new HashMap<>(); //产生式
    private String sentence; // 待分析句型
    private HashMap<String, List<String>> firstvtSets;

    public String getStartSymbol() {
        return startSymbol;
    }


    public List<String> getTerSymbolList() {
        return terSymbolList;
    }

    public List<String> getNonTerSymbolList() {
        return nonTerSymbolList;
    }

    public HashMap<String, Set<String>> getFormulaSetMap() {
        return formulaSetMap;
    }

    public String getSentence() {
        return sentence;
    }

    public HashMap<String, List<String>> getFirstvtSets() {
        return firstvtSets;
    }

    public HashMap<String, List<String>> getLastvtSets() {
        return lastvtSets;
    }

    public String getContent() {
        return content;
    }

    private HashMap<String, List<String>> lastvtSets;
    private boolean[][] F;

    private PriorityTable priorityTable;
    private String content;
    private String analysisResult;
    public OperatorFirstAnalyzer(String content) throws Exception {
        this.content = content;
        analyze();
    }
    public String getAnalysisResult() {
        return analysisResult;
    }

    public PriorityTable getPriorityTable() {
        return priorityTable;
    }

    private void analyze() throws Exception {
        generateDataStructure(content);
        initFirstSetsAndLastSets();
        generateFirstvtSets();
        generateLastvtSets();
        generatePriorityTable();

        String temp = sentence;
        sentence = sentence.replaceAll("\\d+", "i");
        int k = 1;
        int j;
        int sentenceIndex = 0;
        String Q;
        List<String> S = new ArrayList<>(sentence.length() + 5);
        String a;

        for (int i = 0; i < sentence.length() + 5; i++) {
            S.add("");
        }

        S.set(k, "#");

        StringBuilder builder = new StringBuilder();
        do {
            // Print
            builder.append(String.format("%-20s", S.stream().limit(k + 1).reduce((result, element) -> result + element).get()));
            builder.append(String.format("%20s\n", sentence.substring(sentenceIndex)));
            a = String.valueOf(sentence.charAt(sentenceIndex++));

            if (terSymbolList.contains(S.get(k))) {
                j = k;
            } else {
                j = k - 1;
            }
            while (priorityTable.get(S.get(j), a).equals(">")) {
                do {
                    Q = S.get(j);
                    if (terSymbolList.contains(S.get(j - 1))) {
                        j--;
                    } else {
                        j = j - 2;
                    }
                } while (!priorityTable.get(S.get(j), Q).equals("<"));
                k = j + 1;
                S.set(k, "N");
            }
            if (priorityTable.get(S.get(j), a).equals("<") || priorityTable.get(S.get(j), a).equals("=")) {
                k = k + 1;
                S.set(k, a);
            } else {
                throw new Exception(String.format("Invalid sentence: \"%s\"", sentence));
            }
        } while (!a.equals("#"));
        sentence = temp;
        builder.append(String.format("Sentence \"%s\" is valid for the formula\n", sentence));
        analysisResult = builder.toString();
    }

    private void generatePriorityTable() {
        priorityTable = new PriorityTable(terSymbolList);
        for (Map.Entry<String, Set<String>> entry : formulaSetMap.entrySet()) {
            String P = entry.getKey();
            for (String Xs : entry.getValue()) {
                for (int i = 0; i < Xs.length() - 1; i++) {
                    if (isTerSymbol(Xs.charAt(i)) && isTerSymbol(Xs.charAt(i + 1))) {
                        priorityTable.set(Xs.charAt(i), Xs.charAt(i + 1), "=");
                    }
                    if (i < Xs.length() - 2 && isTerSymbol(Xs.charAt(i)) && isTerSymbol(Xs.charAt(i + 2))
                            && isNonTerSymbol(Xs.charAt(i + 1))) {
                        priorityTable.set(Xs.charAt(i), Xs.charAt(i + 2), "=");
                    }
                    if (isTerSymbol(Xs.charAt(i)) && isNonTerSymbol(Xs.charAt(i + 1))) {
                        for (String a : firstvtSets.get(String.valueOf(Xs.charAt(i + 1)))) {
                            priorityTable.set(Xs.charAt(i), a.charAt(0), "<");
                        }
                    }
                    if (isNonTerSymbol(Xs.charAt(i)) && isTerSymbol(Xs.charAt(i + 1))) {
                        for (String a : lastvtSets.get(String.valueOf(Xs.charAt(i)))) {
                            priorityTable.set(a, String.valueOf(Xs.charAt(i + 1)), ">");
                        }
                    }

                }
            }
        }
        // Add values about # for priority
        for (String terSymbol : terSymbolList) {
            if (terSymbol.equals("#")) {
                priorityTable.set(terSymbol, terSymbol, "=");
                continue;
            }
            priorityTable.set(terSymbol, "#", ">");
            priorityTable.set("#", terSymbol, "<");
        }
    }
    private boolean isTerSymbol(char c) {
        return terSymbolList.contains(String.valueOf(c));
    }
    private boolean isNonTerSymbol(char c) {
        return nonTerSymbolList.contains(String.valueOf(c));
    }
    private void generateLastvtSets() {
        F = new boolean[nonTerSymbolList.size()][terSymbolList.size()];
        for (Map.Entry<String, Set<String>> entry : formulaSetMap.entrySet()) {
            String left = entry.getKey();
            Set<String> rights = entry.getValue();
            // “开始时，按照上述规则（1）对每个数组元素F[P, a]赋初值”
            // Iterate formulas
            for (String right : rights) {
                String tail = right.substring(right.length() - 1);
                String penultimate = null;
                if (right.length() > 1) {
                    penultimate = right.substring(right.length() - 2, right.length() - 1);
                }
                if (terSymbolList.contains(tail)) {
                    setGridOfF(left, tail, true);
                } else if (nonTerSymbolList.contains(tail) && terSymbolList.contains(penultimate)) {
                    setGridOfF(left, penultimate, true);
                }
            }
        }
        // “我们用一个栈STACK，把所有初值...”
        Stack<Pair<String, String>> stack = new Stack<>();
        for (int i = 0; i < F.length; i++) {
            for (int j = 0; j < F[i].length; j++) {
                if (F[i][j]) {
                    stack.push(new Pair<>(nonTerSymbolList.get(i), terSymbolList.get(j)));
                }
            }
        }
        // “然后对STACK施行如下运算”
        while (!stack.empty()) {
            Pair<String, String> pair = stack.pop();
            String Q = pair.first();
            String a = pair.second();
            for (Map.Entry<String, Set<String>> entry : formulaSetMap.entrySet()) {
                String P = entry.getKey();
                for (String right : entry.getValue()) {
                    if (right.endsWith(Q)) {
                        if (!getGridOfF(P, a)) {
                            setGridOfF(P, a, true);
                            stack.push(new Pair<>(P, a));
                        }
                    }
                }
            }
        }
        // 依F生成LASTVT
        for (int i = 0; i < F.length; i++) {
            for (int j = 0; j < F[i].length; j++) {
                if (F[i][j]) {
                    lastvtSets.get(nonTerSymbolList.get(i)).add(terSymbolList.get(j));
                }
            }
        }
    }

    private void initFirstSetsAndLastSets() {
        firstvtSets = new HashMap<>();
        for (String nonTerSymbol : nonTerSymbolList) {
            firstvtSets.put(nonTerSymbol, new ArrayList<>());
        }

        lastvtSets = new HashMap<>();
        for (String terSymbol : nonTerSymbolList) {
            lastvtSets.put(terSymbol, new ArrayList<>());
        }
    }

    private void generateFirstvtSets() {
        F = new boolean[nonTerSymbolList.size()][terSymbolList.size()];
        for (Map.Entry<String, Set<String>> entry : formulaSetMap.entrySet()) {
            String left = entry.getKey();
            Set<String> rights = entry.getValue();
            // “开始时，按照上述规则（1）对每个数组元素F[P, a]赋初值”
            // Iterate formulas
            for (String right : rights) {
                String head = right.substring(0, 1);
                String second = null;
                if (right.length() > 1) {
                    second = right.substring(1, 2);
                }
                if (terSymbolList.contains(head)) {
                    setGridOfF(left, head, true);
                } else if (nonTerSymbolList.contains(head) && terSymbolList.contains(second)) {
                    setGridOfF(left, second, true);
                }
            }
        }
        // “我们用一个栈STACK，把所有初值...”
        Stack<Pair<String, String>> stack = new Stack<>();
        for (int i = 0; i < F.length; i++) {
            for (int j = 0; j < F[i].length; j++) {
                if (F[i][j]) {
                    stack.push(new Pair<>(nonTerSymbolList.get(i), terSymbolList.get(j)));
                }
            }
        }
        // “然后对STACK施行如下运算”
        while (!stack.empty()) {
            Pair<String, String> pair = stack.pop();
            String Q = pair.first();
            String a = pair.second();
            for (Map.Entry<String, Set<String>> entry : formulaSetMap.entrySet()) {
                String P = entry.getKey();
                for (String right : entry.getValue()) {
                    if (right.startsWith(Q)) {
                        if (!getGridOfF(P, a)) {
                            setGridOfF(P, a, true);
                            stack.push(new Pair<>(P, a));
                        }
                    }
                }
            }
        }
        // 依F生成FIRSTVT
        for (int i = 0; i < F.length; i++) {
            for (int j = 0; j < F[i].length; j++) {
                if (F[i][j]) {
                    firstvtSets.get(nonTerSymbolList.get(i)).add(terSymbolList.get(j));
                }
            }
        }
    }

    private boolean getGridOfF(String P, String a) {
        return F[nonTerSymbolList.indexOf(P)][terSymbolList.indexOf(a)];
    }
    private void setGridOfF(String P, String a, boolean value) {
        F[nonTerSymbolList.indexOf(P)][terSymbolList.indexOf(a)] = value;
    }

    private void generateDataStructure(String content) {
        Scanner scanner = new Scanner(content);
        int lineIndex = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lineIndex++;
            // process the line
            switch (lineIndex) {
                case 1 -> startSymbol = line;
                case 2 -> nonTerSymbolList = Arrays.stream(line.split(" ")).collect(Collectors.toList());
                case 3 -> terSymbolList = Arrays.stream(line.split(" ")).collect(Collectors.toList());
                default -> {
                    if (!line.endsWith("#")) {
                        String[] splitLine = line.split("->");
                        String left = splitLine[0];
                        String rights = splitLine[1];
                        for (String right : rights.split("\\|")) {
                            if (!formulaSetMap.containsKey(left)) {
                                Set<String> set4left = new HashSet<>();
                                set4left.add(right);
                                formulaSetMap.put(left, set4left);
                            } else {
                                Set<String> set4left = formulaSetMap.get(left);
                                set4left.add(right);
                            }
                        }
                    } else {
                        sentence = line;
                    }
                }
            }
        }
        scanner.close();

    }
}
