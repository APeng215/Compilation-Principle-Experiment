package com.apeng.cpex.ex4;

import com.apeng.cpex.util.FileHelper;

public class Main {
    public static void main(String[] args) {
        String rowString = FileHelper.getFileContentFromResource("file4.txt");
        LRAnalyzer analyzer = new LRAnalyzer(rowString);
        System.out.println(analyzer.getCStr());
        System.out.println(analyzer.getAnalysisTable());
        System.out.println(analyzer.getAnalysisProcess());
    }
}
