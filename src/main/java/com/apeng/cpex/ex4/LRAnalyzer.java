package com.apeng.cpex.ex4;

import com.apeng.cpex.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LRAnalyzer {
    private String startSymbol;//开始符
    private String originalStartSymbol;
    private Set<String> terSymbolSet; // 终结符
    private Set<String> nonTerSymbolSet; // 非终结符
    private final Map<String, Set<String>> formulaSets = new HashMap<>(); //产生式
    private String sentence; // 待分析语句
    private final String rowString; // 输入
    private List<List<Project>> C; // 项目集规范族



    private LRAnalysisTable analysisTable; // LR 分析表
    private AnalysisProcess analysisProcess;
    private boolean analysisSucceed = false;
    public LRAnalyzer(String rowString) {
        this.rowString = rowString;
        this.generateDataStructure();
        this.generateC();
        this.generateAnalysisTable();
        this.analyze();
    }
    public LRAnalysisTable getAnalysisTable() {
        return analysisTable;
    }
    public String getCStr() {
        StringBuilder builder = new StringBuilder();
        for (List<Project> list : C) {
            builder.append(list).append("\n");
        }
        return builder.toString().trim();
    }


    public AnalysisProcess getAnalysisProcess() {
        return analysisProcess;
    }
    public boolean isAnalysisSucceed() {
        return analysisSucceed;
    }

    private void analyze() {
        analysisProcess = new AnalysisProcess();
        Stack<Integer> stateStack = new Stack<>();
        stateStack.add(0);
        Stack<String> symbolStack = new Stack<>();
        symbolStack.add("#");
        int index = 0;
        while (index != sentence.length()) {
            String a = sentence.substring(index, index + 1);
            String operator = analysisTable.getAction(stateStack.peek(), a).first();
            Integer state = analysisTable.getAction(stateStack.peek(), a).second();
            if(operator.equals("s")) { // (1)
                analysisProcess.addStep(stateStack, symbolStack, sentence.substring(index), operator + state);
                stateStack.add(state);
                symbolStack.add(a);
                index++;
            } else if (operator.equals("r")) { // (2)
                analysisProcess.addStep(stateStack, symbolStack, sentence.substring(index), operator + state);
                int formulaIndex = state;
                Pair<String, String> formula2Use = analysisTable.getFormulas().get(formulaIndex);
                int lengthOfRights = formula2Use.second().length();

                for (int i = 0; i < lengthOfRights; i++) {
                    stateStack.pop();
                    symbolStack.pop();
                }
                symbolStack.add(formula2Use.first());
                stateStack.add(analysisTable.getGo2(stateStack.peek(), symbolStack.peek()));
            } else if (operator.equals("acc")) { // (3)
                analysisProcess.addStep(stateStack, symbolStack, sentence.substring(index), "acc");
                analysisSucceed = true;
                break;
            } else {
                throw new RuntimeException("LR analysis fails: The sentence analyzed is unfit to the formulas");
            }
        }

    }

    private void generateAnalysisTable() {
        analysisTable = new LRAnalysisTable(C, terSymbolSet, nonTerSymbolSet, formulaSets);
        for (int k = 0; k < C.size(); k++) {
            List<Project> Ik = C.get(k);
            for (Project project : Ik) {
                // (3)
                if (project.getLeft().equals("W") && !project.hasNextSymbol()) {
                    analysisTable.setAction(k, "#", "acc", -1);
                    continue;
                }
                // (1)
                if (project.hasNextSymbol() && !project.hasNonSymbolAfterDot(nonTerSymbolSet) && C.contains(GO(Ik, project.getNextSymbol()))) {
                    int j = C.indexOf(GO(Ik, project.getNextSymbol()));
                    analysisTable.setAction(k, project.getNextSymbol(), "s", j);
                }
                // (2)
                if (!project.hasNextSymbol()) {
                    for (String a : analysisTable.getTerSymbols()) {
                        analysisTable.setAction(k, a, "r", analysisTable.getFormulas().indexOf(new Pair<>(project.getLeft(), project.getRights())));
                    }
                }
                // (4)
                for (String A : nonTerSymbolSet) {
                    if (C.contains(GO(Ik, A))) {
                        int j = C.indexOf(GO(Ik, A));
                        analysisTable.setGo2(k, A, j);
                    }
                }


            }
        }
    }

    private void generateC() {
        C = new ArrayList<>();
        C.add(CLOSURE(List.of(new Project(startSymbol, originalStartSymbol, 0))));
        List<List<Project>> Ccopy;

        do {
            Ccopy = new ArrayList<>(C);
            for (List<Project> I : Ccopy) {
                for (String X : Stream.concat(nonTerSymbolSet.stream(), terSymbolSet.stream()).collect(Collectors.toSet())) {
                    if (!GO(I, X).isEmpty() && !C.contains(GO(I, X))) {
                        C.add(GO(I, X));
                    }
                }
            }
        } while (!Ccopy.equals(C));

    }
    private List<Project> GO(List<Project> I, String X) {
        List<Project> J = new ArrayList<>();
        I.stream().filter(project -> {
            if (!project.hasNextSymbol()) return false;
            return project.getNextSymbol().equals(X);
        }).forEach(project -> {
            J.add(project.ofNextPosition());
        });
        return CLOSURE(J);
    }
    private List<Project> CLOSURE(List<Project> projects) {
        ArrayList<Project> resultProjects = new ArrayList<>();
        boolean dirty = true;
        while (dirty) {
            dirty = false;
            // (1) I 的任何项目都属于CLOSURE(I)
            for (Project project : projects) {
                dirty = tryToAdd(project, resultProjects) || dirty;
            }
            // (2)
            for (Project project : (ArrayList<Project>)resultProjects.clone()) {
                // 若 A -> ..B.
                String symbolBehindDot = "";
                if (project.hasNextPosition()) {
                    symbolBehindDot = String.valueOf(project.getRights().charAt(project.getDotPosition()));
                }
                if (nonTerSymbolSet.contains(symbolBehindDot)) {
                    String B = String.valueOf(project.getRights().charAt(project.getDotPosition()));
                    for (Map.Entry<String, Set<String>> entry : formulaSets.entrySet()) {
                        // 对于任何关于 B 的产生式 B -> γ...
                        if (entry.getKey().equals(B)) {
                            for (String y : entry.getValue()) {
                                dirty = tryToAdd(new Project(B, y, 0), resultProjects) || dirty;
                            }
                        }
                    }
                }
            }
        }
        return resultProjects;
    }

    private boolean isNonTerSymbol(char c) {
        return nonTerSymbolSet.contains(String.valueOf(c));
    }

    private boolean tryToAdd(Project project, List<Project> projects) {
        if (projects.contains(project)) return false;
        projects.add(project);
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
    private void generateDataStructure() {
        Scanner scanner = new Scanner(rowString);
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
                            if (!formulaSets.containsKey(left)) {
                                Set<String> set4left = new HashSet<>();
                                set4left.add(right);
                                formulaSets.put(left, set4left);
                            } else {
                                Set<String> set4left = formulaSets.get(left);
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

        // 拓广
        nonTerSymbolSet.add("W");
        Set<String> temp = new HashSet<>();
        temp.add(startSymbol);
        formulaSets.put("W", temp);
        originalStartSymbol = startSymbol;
        startSymbol = "W";

    }
}
