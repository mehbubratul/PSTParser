package com.mehbub.lib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;

import static org.junit.jupiter.api.Assertions.*;

class FileParserTest {

    String dsvInputOneFilePath;
    String dsvInputTwoFilePath;
    String invalidFileType;
    String validFileButFirstLineEmpty;
    String mismatchLineCellsWithHeaderRowCells;
    String lastCellOfOneRecordHasNoData;

    @BeforeEach
    void setUp() {
        dsvInputOneFilePath = "src/test/resources/testData/DSV input 1.txt";
        dsvInputTwoFilePath = "src/test/resources/testData/DSV input 2.txt";
        invalidFileType = "src/test/resources/testData/DSV input 2.abc";
        validFileButFirstLineEmpty = "src/test/resources/testData/DSV input 2 - No header.txt";
        mismatchLineCellsWithHeaderRowCells = "src/test/resources/testData/DSV input 2-header-Rows-Mismatch.txt";
        lastCellOfOneRecordHasNoData = "src/test/resources/testData/DSV input 2-LastCellOfARowIsEmptySpace.txt";
    }

    @Test
    void givenValidFilePathAndValidSeparatorWillReturnTrue_1() {
        // given valid file path & valid separator
        String inputFilePath = dsvInputOneFilePath;
        char separator = ',';

        //when
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();

        //then
        assertTrue(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWillReturnTrue_2() {
        // given valid file path & valid separator
        String inputFilePath = dsvInputTwoFilePath;
        char separator = '|';

        //when
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();

        //then
        assertTrue(result.getKey());
    }

    @Test
    void givenInvalidFilePathAndValidSeparatorWillReturnFalse() {
        // given invalid file path & valid separator

        String inputFilePath = invalidFileType;
        char separator = '|';

        //when
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();

        //then
        assertFalse(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWhenFirstLineIsEmptyReturnFalse() {
        // given valid file path & valid separator
        String inputFilePath = validFileButFirstLineEmpty;
        char separator = '|';

        //when
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();

        //then
        assertFalse(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWhenRecordsAreLessComparedWithHeaderFilesWillReturnFalse() {
        // given valid file path & valid separator

        String inputFilePath = mismatchLineCellsWithHeaderRowCells;
        char separator = '|';

        //when
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        System.out.println(result.getValue());
        //then
        assertFalse(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWhenLastCellOfAnyRecordHasNoDataWillReturnTrue() {
        // given valid file path & valid separator
        String inputFilePath = lastCellOfOneRecordHasNoData;
        char separator = '|';
        //when
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        System.out.println(result.getValue());
        //then
        assertTrue(result.getKey());
    }

}