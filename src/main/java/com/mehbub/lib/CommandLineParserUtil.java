package com.mehbub.lib;

import org.apache.commons.cli.*;

import java.util.Objects;

public class CommandLineParserUtil {

    private final int numberOfArguments = 4;

    private String fileName;
    private char separator;

    public String getFileName() {
        return fileName;
    }

    public char getSeparator() {
        return separator;
    }

    public boolean run(String[] args) {

        CommandLine line = parseArguments(args);

        if (line == null) return false;

        if (line.hasOption("filename") && line.hasOption("separator")) {
            fileName = line.getOptionValue("filename");
            if (fileName == null || fileName.equals("")) {
                return false;
            }
            String tempSeparator = line.getOptionValue("separator");
            if (tempSeparator == null || tempSeparator.equals("")) {
                return false;
            }
            if (tempSeparator.length() == 0) {
                return false;
            }
            separator = tempSeparator.charAt(0); //todo : check null or out of index here
            return true;
        }

        showHelp();

        return false;
    }

    private CommandLine parseArguments(String[] args) {
        if (args == null) {
            System.out.println("Validation: Missing required arg!");
            return null;
        }
        if (args.length < numberOfArguments) {
            System.out.println("Validation: Missing minimum required arg!");
            return null;
        }

        for (String arg : args) {
            if (arg == null || Objects.equals(arg, "")) {
                System.out.println("Validation: Argument(s) can not be empty or null!");
                return null;
            }
        }

        Options options = getOptions();
        CommandLine line = null;

        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();

        try {
            line = parser.parse(options, args);

            if ((!line.hasOption("f") && !line.hasOption("s"))) {
                throw new ParseException("Validation: Missing required arg!");
            }
        } catch (ParseException ex) {

            System.err.println("Error: Failed to parse command line arguments");
            showHelp();

            //System.exit(1);
        }

        return line;
    }

    private Options getOptions() {
        var options = new Options();
        options.addOption("f", "filename", true, "file name to load data from");
        options.addOption("s", "separator", true, "separator to separate token from line");
        return options;
    }

    private void showHelp() {
        Options options = getOptions();
        var formatter = new HelpFormatter();
        formatter.printHelp("CommandLineParserUtil", options, true);
    }


}
