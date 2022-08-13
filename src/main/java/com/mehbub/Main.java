package com.mehbub;

import com.mehbub.lib.CommandLineParserUtil;
import com.mehbub.lib.FileParser;

/**
 * Call from console : where the jar located. Change the file path
 * pattern : java -jar jarname -f filename -s separator
 * Copy the below and change appropriately
 * java -jar pstparser-jar-with-dependencies.jar -f "E:\\trial\\pst.ag\\Java task\\DSV input 2.txt" -s "|"
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("... DSV Parser ...");
        System.out.println(" You provided " + args.length + " arguments.\n");

        //doFileProcess(args);
        doFileProcessUsingStream(args);
    }

    private static void doFileProcessUsingStream(String[] args) {
        if (args == null) {
            System.out.println("Validation: Missing required arg!");
            return;
        }

        CommandLineParserUtil commandLineParserUtil = new CommandLineParserUtil();

        boolean isAbleToParse = commandLineParserUtil.run(args);

        if (!isAbleToParse) {
            System.out.println("Unable to parse...");
            return;
        }

        String inputFilePath = commandLineParserUtil.getFileName();
        char separator = commandLineParserUtil.getSeparator();

        FileParser fileParser = new FileParser(inputFilePath, separator);
        fileParser.parseFileUsingStream();
    }

    private static void doFileProcess(String[] args) {
        if (args == null) {
            System.out.println("Validation: Missing required arg!");
            return;
        }

        CommandLineParserUtil commandLineParserUtil = new CommandLineParserUtil();

        boolean isAbleToParse = commandLineParserUtil.run(args);

        if (!isAbleToParse) {
            System.out.println("Unable to parse...");
            return;
        }

        String inputFilePath = commandLineParserUtil.getFileName();
        char separator = commandLineParserUtil.getSeparator();

        FileParser fileParser = new FileParser(inputFilePath, separator);
        fileParser.parseFile();
    }
}