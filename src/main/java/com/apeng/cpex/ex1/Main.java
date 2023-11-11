package com.apeng.cpex.ex1;

import com.apeng.cpex.util.FileHelper;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String rawString = FileHelper.getFileContentFromResource("file.txt");
        System.out.print(new LexicalAnalysis(rawString).analyze());
    }
}
