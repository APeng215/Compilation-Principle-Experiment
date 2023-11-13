package com.apeng.cpex.ex3;

import com.apeng.cpex.util.FileHelper;
import com.apeng.cpex.util.Pair;
import com.apeng.cpex.util.PriorityTable;

import java.util.*;
import java.util.stream.Collectors;



public class Main {

    public static void main(String[] args) {
        String content = FileHelper.getFileContentFromResource("file3.txt");
        OperatorFirstAnalyzer analyzer = new OperatorFirstAnalyzer(content);
        try {
            analyzer.analyze();
        } catch (Exception e) {
            e.printStackTrace();
        }
        analyzer.getPriorityTable().print();
        System.out.print(analyzer.getAnalysisResult());
        System.out.print(analyzer.getFirstvtSets());
    }


}
