package com.apeng.cpex.ex1;

import com.apeng.cpex.util.FileHelper;

public class Main {
    public static void main(String[] args) {
        String rawString = FileHelper.getFileContentFromResource("file.txt");
        LexicalAnalyzer analyzer = new LexicalAnalyzer(rawString);
        analyzer.analyze();
        System.out.print(analyzer.getResult());
    }
}
