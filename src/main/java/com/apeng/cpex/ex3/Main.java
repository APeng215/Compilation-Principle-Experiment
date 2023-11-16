package com.apeng.cpex.ex3;

import com.apeng.cpex.util.FileHelper;


public class Main {

    public static void main(String[] args) {
        String content = FileHelper.getFileContentFromResource("file3.txt");
        try {
            OperatorFirstAnalyzer analyzer = new OperatorFirstAnalyzer(content);
            analyzer.getPriorityTable().print();
            System.out.println(analyzer.getAnalysisResult());
            System.out.println(analyzer.getFirstvtSets());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
