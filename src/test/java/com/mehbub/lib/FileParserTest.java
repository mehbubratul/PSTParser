package com.mehbub.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileParserTest {

    //region private fields
    String dsvInputOneFilePath;
    String dsvInputTwoFilePath;
    String invalidFileType;
    String validFileButFirstLineEmpty;
    String mismatchLineCellsWithHeaderRowCells;
    String lastCellOfOneRecordHasNoData;
    String dsv_100000_Rows;
    //endregion

    @BeforeEach
    void setUp() {
        dsvInputOneFilePath = "src/test/resources/testData/DSV input 1.txt";
        dsvInputTwoFilePath = "src/test/resources/testData/DSV input 2.txt";
        invalidFileType = "src/test/resources/testData/DSV input 2.abc";
        validFileButFirstLineEmpty = "src/test/resources/testData/DSV input 2 - No header.txt";
        mismatchLineCellsWithHeaderRowCells = "src/test/resources/testData/DSV input 2-header-Rows-Mismatch.txt";
        lastCellOfOneRecordHasNoData = "src/test/resources/testData/DSV input 2-LastCellOfARowIsEmptySpace.txt";
        dsv_100000_Rows = "src/test/resources/testData/DSV_100000_Rows.txt";
    }

    //region Test methods
    @Test
    void givenValidFilePathAndValidSeparatorWillReturnTrue_1() {
        // given valid file path & valid separator
        String inputFilePath = dsvInputOneFilePath;
        char separator = ',';

        //when
        // AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFileUsingStreamSupport();

        //then
        assertTrue(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWillReturnTrue_2() {
        // given valid file path & valid separator
        String inputFilePath = dsvInputTwoFilePath;
        char separator = '|';

        //when
        // AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFileUsingStreamSupport();

        //then
        assertTrue(result.getKey());
    }

    @Test
    void givenInvalidFilePathAndValidSeparatorWillReturnFalse() {
        // given invalid file path & valid separator

        String inputFilePath = invalidFileType;
        char separator = '|';

        // when
        // AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFileUsingStreamSupport();

        // then
        assertFalse(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWhenFirstLineIsEmptyReturnFalse() {
        // given valid file path & valid separator
        String inputFilePath = validFileButFirstLineEmpty;
        char separator = '|';

        //  when
        // AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFileUsingStreamSupport();

        //then
        assertFalse(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWhenRecordsAreLessComparedWithHeaderFilesWillReturnFalse() {
        // given valid file path & valid separator

        String inputFilePath = mismatchLineCellsWithHeaderRowCells;
        char separator = '|';

        // when
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();

        // then
        assertFalse(result.getKey());
    }

    @Test
    void givenValidFilePathAndValidSeparatorWhenLastCellOfAnyRecordHasNoDataWillReturnTrue() {
        // given valid file path & valid separator
        String inputFilePath = lastCellOfOneRecordHasNoData;
        char separator = '|';
        // when
        // AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFileUsingStreamSupport();

        //then
        assertTrue(result.getKey());
    }

    @Test
    void StringWithDoubleQuotation() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> obj = new LinkedHashMap<>();
        String key = "middleName";
        Object value = "Sk??odowska |";
        obj.put(key, value);
        String s = mapper.writeValueAsString(obj);
        System.out.println(s);
    }

    @Test
    void givenValidFilePathAndValidSeparatorAndLargeDataWillReturnTrue_1() {
        // given valid file path & valid separator

        char separator = ',';

        //when
        // AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(inputFilePath, separator).parseFile();
        AbstractMap.SimpleEntry<Boolean, String> result = new FileParser(dsv_100000_Rows, separator).parseFileUsingStreamSupport();

        //then
        assertTrue(result.getKey());
    }
    //endregion

}