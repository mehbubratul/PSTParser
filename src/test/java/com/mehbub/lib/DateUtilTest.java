package com.mehbub.lib;

import org.junit.jupiter.api.Test;

import java.util.AbstractMap;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void givenStringWillReturnFalse(){
        // given
        String value = "abc";
        // when
        AbstractMap.SimpleEntry<Boolean, String> result = DateUtil.DateValidation(value);
        // then
        assertFalse(result.getKey());
    }

    @Test
    void givenStringWillReturnEmptyString(){
        // given
        String value = "abc";
        // when
        AbstractMap.SimpleEntry<Boolean, String> result = DateUtil.DateValidation(value);
        // then
        assertEquals(result.getValue(), "");
    }

    @Test
    void givenDateWillReturnTrue(){
        // given
        String value = "01-02-1998";
        // when
        AbstractMap.SimpleEntry<Boolean, String> result = DateUtil.DateValidation(value);
        // then
        assertTrue(result.getKey());
    }

    @Test
    void givenDateWillReturnRequiredFormattedDate(){
        // given
        String actualValue = "01/02/1998";
        String expectedValue = "1998-02-01";
        // when
        AbstractMap.SimpleEntry<Boolean, String> result = DateUtil.DateValidation(actualValue);
        // then
        assertEquals(result.getValue(), expectedValue);
    }

}