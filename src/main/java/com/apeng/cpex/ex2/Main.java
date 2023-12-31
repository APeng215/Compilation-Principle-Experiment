package com.apeng.cpex.ex2;

import com.apeng.cpex.util.FileHelper;

public class Main {

    public static void main(String[] args) {
        String content = FileHelper.getFileContentFromResource("file2.txt");
        LLAnalyzer analyzer = new LLAnalyzer(content);
        System.out.println(analyzer.getFirstSetsStr() + "\n");
        System.out.println(analyzer.getFollowSetsStr());
        System.out.println(analyzer.getAnalyzingTableStr());
        System.out.println(analyzer.getAnalysisResult());
    }

}
