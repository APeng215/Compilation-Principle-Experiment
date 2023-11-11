package com.apeng.cpex.util;

import com.apeng.cpex.ex1.Main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHelper {
    public static String getFileContentFromResource(String locationInResource) {
        try {
            return new String(Files.readAllBytes(Paths.get(Main.class.getClassLoader().getResource(locationInResource).toURI())));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
