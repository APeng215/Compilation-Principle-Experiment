package com.apeng.cpex.ex3;

import com.apeng.cpex.util.FileHelper;


public class Main {

    public static void main(String[] args) {
        String content = FileHelper.getFileContentFromResource("file3.txt");
        try {
            OperatorFirstAnalyzer analyzer = new OperatorFirstAnalyzer(content);

            System.out.println(analyzer.isSentenceCorrect());
            System.out.println(analyzer.getFirstvtSetStr());
            System.out.println(analyzer.getLastvtSetStr());
            System.out.println(analyzer.getPriorityTableStr());
            System.out.println(analyzer.getAnalysisResult());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
