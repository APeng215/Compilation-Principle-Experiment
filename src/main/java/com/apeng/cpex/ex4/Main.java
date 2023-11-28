package com.apeng.cpex.ex4;

import com.apeng.cpex.util.FileHelper;

public class Main {
    public static void main(String[] args) {
        String rowString = FileHelper.getFileContentFromResource("file4.txt");
        LRAnalyzer analyzer = new LRAnalyzer(rowString);
        System.out.println("项目集规范族:");
        System.out.println(analyzer.getCStr());
        System.out.println("LR(0)分析表:");
        System.out.println(analyzer.getAnalysisTable());
        System.out.println("LR(0)分析过程:");
        System.out.println(analyzer.getAnalysisProcess());
    }
}
