package com.apeng.cpex.ex1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    public String rwtab = String.join("|", "main", "if", "then", "while", "do", "static", "int",
            "double", "struct", "break", "else", "long", "switch",
            "case", "typedef", "char", "return", "const", "float",
            "short", "continue", "for", "void", "default",
            "sizeof", "do");



    private Map<String, Integer> key2Code = new HashMap<>() {{
        put("main", 1);
        put("if", 2);
        put("then", 3);
        put("while", 4);
        put("do", 5);
        put("static", 6);
        put("ID", 25);
        put("NUM", 26);
        put("+", 27);
        put("-", 28);
        put("*", 29);
        put("/", 30);
        put(":", 31);
        put(":=", 32);
        put("<", 33);
        put("<>", 34);
        put("<=", 35);
        put(">", 36);
        put(">=", 37);
        put("=", 38);
        put("default", 39);
        put(";", 41);
        put("(", 42);
        put(")", 43);
        put("int", 7);
        put("double", 8);
        put("struct", 9);
        put("break", 10);
        put("else", 11);
        put("long", 12);
        put("switch", 13);
        put("case", 14);
        put("typedef", 15);
        put("char", 16);
        put("return", 17);
        put("const", 18);
        put("float", 19);
        put("short", 20);
        put("continue", 21);
        put("for", 22);
        put("void", 23);
        put("sizeof", 24);
        put("#", 0);
    }};
    public String keywordsRegex = "main|if|then|while|do|static|int|double|struct|break|else|long|switch|case|typedef|char|return|const|float|short|continue|for|void|default|sizeof";
    public String symbolRegex = "\\+|-|\\*|/|:|:=|<|<>|<=|>|>=|=|;|\\(|\\)|#";
    public String IDRegex = "[a-zA-Z]\\w*";
    public String NUMRegex = "\\d+";

    public String excludeIdNum = "struct|<=|<>|:=|const|for|main|do|while|float|long|switch|default|else|continue|if|case|static|void|#|break|sizeof|double|\\(|\\)|\\*|then|\\+|-|typedef|int|/|char|short|:|;|<|=|>|return|>=";

    private String result;
    private String rawString;
    private String preProcessedString;
    public LexicalAnalyzer(String rawString) {
        this.rawString = rawString;
        analyze();
    }

    public String getPreProcessedString() {
        return preProcessedString;
    }

    public String getKey2CodeStr() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : key2Code.entrySet()) {
            builder.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        return builder.toString().trim();
    }
    public String getResult() {
        return result.trim();
    }
    private void analyze() {
        assert rawString != null;
        // 预处理
        preProcessedString = preProcess(rawString);
        // 统一全部识别出来存入 identified
        Matcher matcher = Pattern.compile(keywordsRegex + "|" + symbolRegex + "|" + IDRegex + "|" + NUMRegex).matcher(preProcessedString);
        List<String> identified = new LinkedList<>();
        while (matcher.find()) {
            identified.add(matcher.group());
        }
        StringBuilder builder = new StringBuilder();
        // 遍历 identified，分类之
        for (String str : identified) {
            if (isKeywords(str) || isSymbol(str)) {
                builder.append(String.format("( %d : %s )\n", key2Code.get(str), str));
            } else if (isNUM(str)) {
                builder.append(String.format("( %d : %s )\n", key2Code.get("NUM"), str));
            } else {
                builder.append(String.format("( %d : %s )\n", key2Code.get("ID"), str));
            }
        }
        result = builder.toString();

    }


    String preProcess(String input) {
        return input
                .replaceAll("//.*\r\n", "")
                .replaceAll("[\n\r\t]", " ")
                .replaceAll(" +", " ");

    }

    boolean isKeywords(String input) {
        return input.matches(keywordsRegex);
    }

    boolean isSymbol(String input) {
        return input.matches(symbolRegex);
    }

    boolean isID(String input) {
        return input.matches(IDRegex);
    }

    boolean isNUM(String input) {
        return input.matches(NUMRegex);
    }
}
