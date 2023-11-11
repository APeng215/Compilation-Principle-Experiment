package com.apeng.cpex.ex2;

import com.apeng.cpex.util.AnalysisResult;
import com.apeng.cpex.util.FileHelper;

import java.util.*;
import java.util.stream.Collectors;

public class LLAnalyzer {
    private static String startSymbol;//开始符
    private static Set<String> terSymbolSet; // 终结符
    private static Set<String> nonTerSymbolSet; // 非终结符
    private static final HashMap<String, Set<String>> formulaSetMap = new HashMap<>(); //产生式
    private static final HashMap<String, Set<String>> firstSetMap = new HashMap<>();
    private static final HashMap<String, Set<String>> followSetMap = new HashMap<>();
    private static final HashMap<String, HashMap<String, ArrayList<String>>> analyzingTable = new HashMap<>();
    private String sentence;
    private String content;
    private final AnalysisResult analysisResult = new AnalysisResult();
    public LLAnalyzer(String input) {
        this.content = input;
    }

    public AnalysisResult getAnalysisResult() {
        return analysisResult;
    }
    public void analyze() {
        generateDataStructure(content);
        generateFirstSetMap();
        generateFollowSetMap();
        initAnalyzingTable();
        generateAnalyzingTable();

        Stack<String> stack = new Stack<>();
        stack.push("#");
        stack.push(startSymbol);
        analysisResult.add(toFlatString(stack), sentence, "");
        int i = 0;
        while (i != sentence.length()) {
            String a = sentence.substring(i, i + 1);
            String X = stack.peek();
            if (a.equals("#") && X.equals("#")) {
                System.out.format("Analysis succeeds!\n");
                break;
            }
            if (a.equals(X)) {
                i++;
                stack.pop();
                analysisResult.add(toFlatString(stack), sentence.substring(i), "");
                continue;
            }
            if (nonTerSymbolSet.contains(X)) {
                try {
                    String grid = analyzingTable.get(X).get(a).get(0);
                    stack.pop();
                    for (Character character : new StringBuilder(grid).reverse().toString().toCharArray()) {
                        if (character.equals('ε')) continue;
                        stack.push(character.toString());
                    }
                    analysisResult.add(toFlatString(stack), sentence.substring(i), X + "->" + grid);
                } catch (IndexOutOfBoundsException e) {
                    System.out.format("错误：分析表[%s, %s]为空！\n", a, X);
                    e.printStackTrace();
                }

            }

        }
    }

    private String toFlatString(Stack<String> stack) {
        return stack.stream().reduce((result, element) -> result + element).orElse("");

    }


    private void generateAnalyzingTable() {
        for (Map.Entry<String, Set<String>> entry : formulaSetMap.entrySet()) {
            String left = entry.getKey();
            Set<String> rights = entry.getValue();
            // 对于文法每个产生式
            for (String right : rights) {
                // (2)
                for (String firstSymbol : getFirstSet(right)) {
                    analyzingTable.get(left).get(firstSymbol).add(right);
                }
                // (3)
                if (getFirstSet(right).contains("ε")) {
                    for (String followSymbol : followSetMap.get(left)) {
                        analyzingTable.get(left).get(followSymbol).add(right);
                    }
                }
            }
        }
    }

    private void initAnalyzingTable() {
        for (String nonTerSymbol : nonTerSymbolSet) {
            HashMap<String, ArrayList<String>> row = new HashMap<>();
            for (String terSymbol : terSymbolSet) {
                ArrayList<String> list = new ArrayList<>();
                row.put(terSymbol, list);
            }
            row.put("#", new ArrayList<>());
            analyzingTable.put(nonTerSymbol, row);
        }
    }

    private void generateFollowSetMap() {
        boolean dirty = true;
        while (dirty) {
            dirty = false;
            for (Map.Entry<String, Set<String>> formula : formulaSetMap.entrySet()) {
                String left = formula.getKey();
                Set<String> rights = formula.getValue();
                // (1)
                if (startSymbol.equals(left)) {
                    dirty = try2add("#", followSetMap.get(left)) || dirty;
                }
                for (String right : rights) {
                    // (2)
                    for (int i = 0; i < right.length() - 1; i ++) {
                        if (nonTerSymbolSet.contains(right.substring(i, i + 1))) {
                            Set<String> toAdd = getFirstSet(right.substring(i + 1));
                            toAdd.remove("ε");
                            for (String str : toAdd) {
                                dirty = try2add(str, followSetMap.get(right.substring(i, i + 1))) || dirty;
                            }
                        }

                        if (nonTerSymbolSet.contains(right.substring(i, i + 1)) && getFirstSet(right.substring(i + 1)).contains("ε")) {
                            Set<String> toAdd = followSetMap.get(left);
                            for (String str : toAdd) {
                                dirty = try2add(str, followSetMap.get(right.substring(i, i + 1))) || dirty;
                            }
                        }

                    }
                    // (3)
                    if (nonTerSymbolSet.contains(right.substring(right.length() - 1))) {
                        Set<String> toAdd = followSetMap.get(left);
                        for (String str : toAdd) {
                            dirty = try2add(str, followSetMap.get(right.substring(right.length() - 1))) || dirty;
                        }
                    }
                }
            }
        }
    }

    private Set<String> getFirstSet(String symbolStr){
        if (symbolStr.equals("ε")) return new HashSet<>(Collections.singleton("ε"));
        Set<String> resultSet = new HashSet<>(firstSetMap.get(symbolStr.substring(0, 1)));
        resultSet.remove("ε");
        for (int i = 1; i < symbolStr.length(); i++) {
            if (isAllContainsE(symbolStr.substring(0, i))) {
                Set<String> copySet = new HashSet<>(firstSetMap.get(symbolStr.substring(i, i + 1)));
                copySet.remove("ε");
                resultSet.addAll(copySet);
            }
        }
        if (isAllContainsE(symbolStr)) {
            resultSet.add("ε");
        }
        return resultSet;
    }

    private void generateFirstSetMap() {
        boolean dirty = true;
        while (dirty) {
            dirty = false;
            for (Map.Entry<String, Set<String>> formula : formulaSetMap.entrySet()) {
                String left = formula.getKey();
                Set<String> rights = formula.getValue();
                // (1)
                if (terSymbolSet.contains(left)) {
                    dirty = try2add(left, firstSetMap.get(left)) || dirty;
                } else { // (2)
                    for (String right : rights) {
                        for (String terSymbol : terSymbolSet) {
                            if (Objects.equals(terSymbol, right.substring(0, 1))) {
                                dirty = try2add(terSymbol, firstSetMap.get(left)) || dirty;
                                break;
                            }
                        }
                        if (Objects.equals(right, "ε")) {
                            dirty = try2add(right, firstSetMap.get(left)) || dirty;
                        }
                    }
                }
                // (3)
                for (String right : rights) {
                    if (nonTerSymbolSet.contains(right.substring(0, 1))) {
                        for (String first : firstSetMap.get(right.substring(0, 1))) {
                            if (!Objects.equals(first, "ε")) dirty = try2add(first, firstSetMap.get(left)) || dirty;
                        }
                    }
                    // 则把 FIRST(Y) 中的所有非 e 元素都加到 FIRST(X) 中
                    for (int last = 1; last < right.length(); last++) {
                        String subString = right.substring(0, last);
                        if (isAllNoneTerminal(subString) && isAllContainsE(subString)) {
                            Set<String> firstSet = firstSetMap.get(right.substring(last));
                            for (String first : firstSet) {
                                if (!Objects.equals(first, "ε")) dirty = try2add(first, firstSetMap.get(left)) || dirty;
                            }
                        }
                    }
                    // 特别是...
                    if (isAllNoneTerminal(right) && isAllContainsE(right)) {
                        dirty = try2add("ε", firstSetMap.get(left)) || dirty;
                    }
                }
            }
        }
        // 单独处理终结符
        for (String terSymbol : terSymbolSet) {
            firstSetMap.get(terSymbol).add(terSymbol);
        }
    }

    private boolean isAllContainsE(String string) {
        boolean isAllContainsE = true;
        for (Character y : string.toCharArray()) {
            Set<String> firstSet = firstSetMap.get(y.toString());
            if (!firstSet.contains("ε")) {
                isAllContainsE = false;
                break;
            }
        }
        return isAllContainsE;
    }

    private boolean isAllNoneTerminal(String string) {
        boolean isAllNoneTerminal = true;
        for (Character y : string.toCharArray()) {
            if (terSymbolSet.contains(y.toString())) {
                isAllNoneTerminal = false;
                break;
            }
        }
        return isAllNoneTerminal;
    }

    private boolean try2add(String value, Set<String> targetSet) {
        if (targetSet.contains(value)) return false;
        targetSet.add(value);
        return true;
    }

    /*
        Content 的要求：
            第 1 行：开始符号
            第 2 行：非终结符（用空格隔开）
            第 3 行：终结符（用空格隔开）
            余下每行：产生式（形如 F->(E)|i ）
            最后一行：表达式（用 # 结尾，形如 (i+i)# ）
    */
    private void generateDataStructure(String content) {
        Scanner scanner = new Scanner(content);
        int lineIndex = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lineIndex++;
            // process the line
            switch (lineIndex) {
                case 1 -> startSymbol = line;
                case 2 -> nonTerSymbolSet = Arrays.stream(line.split(" ")).collect(Collectors.toSet());
                case 3 -> terSymbolSet = Arrays.stream(line.split(" ")).collect(Collectors.toSet());
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
        for (String symbol : terSymbolSet) {
            firstSetMap.put(symbol, new HashSet<>());
            followSetMap.put(symbol, new HashSet<>());
        }
        for (String symbol : nonTerSymbolSet) {
            firstSetMap.put(symbol, new HashSet<>());
            followSetMap.put(symbol, new HashSet<>());
        }
    }

}
