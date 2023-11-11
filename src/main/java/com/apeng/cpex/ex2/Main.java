package com.apeng.cpex.ex2;

import com.apeng.cpex.util.FileHelper;
import com.apeng.cpex.util.AnalysisResult;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String content = FileHelper.getFileContentFromResource("file2.txt");
        LLAnalyzer analyzer = new LLAnalyzer(content);
        analyzer.analyze();
        AnalysisResult analysisResult = analyzer.getAnalysisResult();
        System.out.print(analysisResult);
    }

}
