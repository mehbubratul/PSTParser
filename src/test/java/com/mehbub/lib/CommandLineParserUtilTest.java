package com.mehbub.lib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineParserUtilTest {

    //  java -jar pstparser-jar-with-dependencies.jar -f "E:\\trial\\pst.ag\\Java task\\DSV input 2.txt" -s "|"
    final int numberOfArguments = 4;

    String[] nullOrEmptyArgs;
    String[] validArgs;
    String[] inValidArgs;
    String[] lessThenRequiredValidArgs;
    String[] moreThenArgsWithAllRequiredArgs;
    File dsvInputOneFile;
    File dsvInputTwoFile;
    String dsvInputOneFilePath;
    String dsvInputTwoFilePath;

    @BeforeEach
    void setUp() {
        dsvInputOneFilePath = "src/test/resources/testData/DSV input 1.txt";
        dsvInputTwoFilePath = "src/test/resources/testData/DSV input 2.txt";
        dsvInputOneFile = new File("src/test/resources/testData/DSV input 1.txt");
        dsvInputTwoFile = new File("src/test/resources/testData/DSV input 2.txt");

        getNullOrEmptyArgs();

        getValidArgs();

        getInvalidArgs();

        getLessThenRequiredValidArgs();

        getMoreThenArgsWithAllRequiredArgs();
    }

    private void getNullOrEmptyArgs() {
        inValidArgs = new String[5];
        inValidArgs[0] = null;
        inValidArgs[1] = null;
        inValidArgs[2] = null;
        inValidArgs[3] = null;
    }

    private void getMoreThenArgsWithAllRequiredArgs() {
        moreThenArgsWithAllRequiredArgs = new String[5];
        moreThenArgsWithAllRequiredArgs[0] = "-f";
        moreThenArgsWithAllRequiredArgs[1] = "E:\\trial\\pst.ag\\Java task\\DSV input 2.txt";
        moreThenArgsWithAllRequiredArgs[2] = "-s";
        moreThenArgsWithAllRequiredArgs[3] = "|";
        moreThenArgsWithAllRequiredArgs[4] = "|";
    }

    private void getLessThenRequiredValidArgs() {
        lessThenRequiredValidArgs = new String[numberOfArguments];
        lessThenRequiredValidArgs[0] = "-f";
        lessThenRequiredValidArgs[1] = dsvInputTwoFilePath;
        lessThenRequiredValidArgs[2] = "-s";
        // here, separator is missing;
    }

    private void getInvalidArgs() {
        inValidArgs = new String[numberOfArguments];
        inValidArgs[0] = "-a"; // This is invalid
        inValidArgs[1] = dsvInputTwoFilePath;
        inValidArgs[2] = "-s";
        inValidArgs[3] = "|";
    }

    private void getValidArgs() {
        validArgs = new String[numberOfArguments];
        validArgs[0] = "-f";
        validArgs[1] = dsvInputTwoFilePath;
        validArgs[2] = "-s";
        validArgs[3] = "|";
    }

    @Test
    void givenNullArgumentThenUnableToParseCMDArguments() {
        // given :  null as args
        // when
        boolean run = new CommandLineParserUtil().run(nullOrEmptyArgs);
        // then
        assertFalse(run);
    }

    @Test
    void givenNullOrEmptyValueInArgumentsThenUnableToParseCMDArguments() {
        // given :  null as args
        // when
        boolean run = new CommandLineParserUtil().run(null);
        // then
        assertFalse(run);
    }

    @Test
    void givenValidArgumentThenAbleToParseCMDArguments() {
        // given : validArgs
        // args
        // when
        boolean run = new CommandLineParserUtil().run(validArgs);
        // then
        assertTrue(run);
    }

    @Test
    void givenInValidArgumentThenUnableToParseCMDArguments() {
        // given : inValidArgs
        // when
        boolean run = new CommandLineParserUtil().run(inValidArgs);
        // then
        assertFalse(run);
    }

    @Test
    void givenLessThanRequiredArgumentThenUnableToParseCMDArguments() {
        // given : inValidArgs
        // when
        boolean run = new CommandLineParserUtil().run(lessThenRequiredValidArgs);
        // then
        assertFalse(run);
    }

    @Test
    void givenMoreThenArgsWithAllRequiredArgsThenAbleToParseCMDArguments() {
        // given : inValidArgs
        // when
        boolean run = new CommandLineParserUtil().run(moreThenArgsWithAllRequiredArgs);
        // then
        assertTrue(run);
    }
}