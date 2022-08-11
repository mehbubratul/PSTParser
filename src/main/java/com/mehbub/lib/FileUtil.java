package com.mehbub.lib;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;

public class FileUtil {

    private final String inputFilePath;

    public FileUtil(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public AbstractMap.SimpleEntry<Boolean, String> fileValidation() {
        Path path = Paths.get(inputFilePath);

        if (!Files.exists(path)) {
            return new AbstractMap.SimpleEntry<>(false, "File doesn't exist");
        }

        if (Files.isDirectory(path)) {
            return new AbstractMap.SimpleEntry<>(false, "File exists, but it is a directory.");
        }

        if (Files.isRegularFile(path)) {
            return new AbstractMap.SimpleEntry<>(true, "File exists!");
        }

        return new AbstractMap.SimpleEntry<>(false, "Unable to validate file!");
    }

    public String getOutFilePath() {
        String inputFileName = Paths.get(inputFilePath).getFileName().toString();
        String outFileName = inputFileName.split("\\.")[0];

        StringBuilder sb = new StringBuilder(Paths.get("").toAbsolutePath().toString());
        sb.append("\\");
        sb.append(outFileName);
        sb.append(".JSONL");

//        Path currentRelativePath = Paths.get("");
//        String s = currentRelativePath.toAbsolutePath().toString().concat("\\");
//        String outPath = s.concat(inputFileName.split("\\.")[0]).concat(".JSONL");
        return sb.toString();
    }
}
