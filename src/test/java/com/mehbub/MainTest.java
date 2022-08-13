package com.mehbub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

/***
 *
 */

class MainTest {
    final int numberOfArguments = 4;
    File dsvInputOneFile;
    File dsvInputTwoFile;
    String dsvInputOneFilePath;
    String dsvInputTwoFilePath;
    Main app;

    @BeforeEach
    void setUp() {
        dsvInputOneFilePath = "src/test/resources/testData/DSV input 1.txt";
        dsvInputTwoFilePath = "src/test/resources/testData/DSV input 2.txt";
        dsvInputOneFile = new File("src/test/resources/testData/DSV input 1.txt");
        dsvInputTwoFile = new File("src/test/resources/testData/DSV input 2.txt");
        app = new Main();
    }

    private String[] getArgsWhenSeparatorValueIsEmpty() {
        String[] args = new String[numberOfArguments];
        args[0] = "-f";
        args[1] = dsvInputTwoFilePath;
        args[2] = "";
        args[3] = "";
        return args;
    }

    private String[] getArgsWhenFilePathValueIsEmpty() {
        String[] args = new String[numberOfArguments];
        args[0] = "-f";
        args[1] = "";
        args[2] = "-s";
        args[3] = "|";
        return args;
    }

    private String[] getInvalidArgsWithAllValuesEmpty() {
        String[] args = new String[numberOfArguments];
        args[0] = "";
        args[1] = "";
        args[2] = "";
        args[3] = "";
        return args;
    }

    private String[] getValidArgsSampleFileOne() {
        String[] args = new String[numberOfArguments];
        args[0] = "-f";
        args[1] = dsvInputOneFilePath;
        args[2] = "-s";
        args[3] = ",";
        return args;
    }

    private String[] getValidArgsSampleFileTwo() {
        String[] args = new String[numberOfArguments];
        args[0] = "-f";
        args[1] = dsvInputTwoFilePath;
        args[2] = "-s";
        args[3] = "|";
        return args;
    }

    @Test
    void mainWithValidArgsWithSampleFileTwo() {
        app.main(getValidArgsSampleFileTwo());
    }

    @Test
    void mainWithValidArgsWithSampleFileOne() {
        app.main(getValidArgsSampleFileOne());
    }

    @Test
    void mainWhenInvalidArgsWithAllValuesEmpty() {
        app.main(getInvalidArgsWithAllValuesEmpty());
    }

    @Test
    void mainWhenSeparatorValueIsEmpty() {
        app.main(getArgsWhenSeparatorValueIsEmpty());
    }

    @Test
    void mainWhenFilePathValueIsEmpty() {
        app.main(getArgsWhenFilePathValueIsEmpty());
    }
}