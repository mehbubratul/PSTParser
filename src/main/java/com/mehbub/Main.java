package com.mehbub;

import com.mehbub.lib.CommandLineParserUtil;
import com.mehbub.lib.FileParser;

import java.util.AbstractMap;

/**
 * Call from console : where the jar located. Change the file path
 * pattern : java -jar jarname -f filename -s separator
 * Copy the below and change appropriately
 * java -jar pstparser-jar-with-dependencies.jar -f "E:\\trial\\pst.ag\\Java task\\DSV input 2.txt" -s "|"
 * java -jar pstparser-jar-with-dependencies.jar -f "E:\\trial\\pst.ag\\Java task\\DSV input 1.txt" -s ","
 *  * java -jar pstparser-jar-with-dependencies.jar -f "E:\\trial\\pst.ag\\Java task\\DSV_100000_Rows.txt" -s ","
 */
public class Main {

    public static void main(String[] args) {

        System.out.println(" File Parser ...");
        System.out.println(" Arguments provided " + args.length + "\n");

        doFileProcessUsingStream(args);
    }

    //region private methods
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
        AbstractMap.SimpleEntry<Boolean, String> result = fileParser.parseFileUsingStreamSupport();
        System.out.println(result.getValue());

    }

    @Deprecated
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
    //endregion
}