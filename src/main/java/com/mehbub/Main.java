package com.mehbub;

import com.mehbub.lib.CommandLineParserUtil;
import com.mehbub.lib.FileParser;

public class Main {

    public static void main(String[] args) {

        System.out.println("... DSV Parser ...");
        System.out.println(" You provided " + args.length + " arguments.\n");

        doFileProcess(args);

        System.out.println("... File Parsing Done. ...");
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