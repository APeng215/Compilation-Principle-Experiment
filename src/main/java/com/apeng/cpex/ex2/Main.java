package com.apeng.cpex.ex2;

import com.apeng.cpex.util.FileHelper;

public class Main {

    public static void main(String[] args) {
        String content = FileHelper.getFileContentFromResource("file2.txt");
        LLAnalyzer analyzer = new LLAnalyzer(content);
        AnalysisResult analysisResult = analyzer.getAnalysisResult();
        System.out.print(analysisResult);
    }

}
