package com.mehbub.lib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.AbstractMap;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

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
    }

    @Test
    void givenValidFilePathWillReturnTrue() {
        // given = valid file path -> inputFilePath
        String inputFilePath = dsvInputTwoFilePath;
        // when
        FileUtil fileUtil = new FileUtil(inputFilePath);
        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();
        // then
        assertTrue(result.getKey());
    }

    @Test
    void givenValidFileDirectoryWithoutFileNameWillReturnFalse() {
        // given = valid file directory -> inputFilePath
        String inputFilePath = "E:\\trial\\pst.ag\\Java task\\";
        // when
        FileUtil fileUtil = new FileUtil(inputFilePath);
        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();
        // then
        assertFalse(result.getKey());
    }

    @Test
    void givenInValidFileTypeWillReturnFalse() {
        // given = in valid file name -> inputFilePath
        String inputFilePath = "E:\\trial\\pst.ag\\Java task\\DSV input 2.abc";
        // when
        FileUtil fileUtil = new FileUtil(inputFilePath);
        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();
        // then
        assertFalse(result.getKey());
    }

    @Test
    void givenValidFileWillReturnValidOutFileString() {
        // given
        String inputFilePath = dsvInputTwoFilePath;
        String expectedOutFilePath = "E:\\git\\PSTParser\\DSV input 2.JSONL";
        // when
        FileUtil fileUtil = new FileUtil(inputFilePath);
        String resultString = fileUtil.getOutFilePath();
        // then
        assertEquals(resultString, expectedOutFilePath);
    }

    @Test
    void getOutputFileFromInputFile() {
        String inputFilePath = dsvInputTwoFilePath;
        String inputFileName = Paths.get(inputFilePath).getFileName().toString();
        String outFileName = inputFileName.split("\\.")[0];

        StringBuilder sb = new StringBuilder(Paths.get("").toAbsolutePath().toString());
        sb.append("\\");
        sb.append(outFileName);
        sb.append(".JSONL");
        System.out.println(sb);
    }

//    @Test
//    void getFileFromTestResourceDirectory() {
//        Path resourceDirectory = Paths.get("src", "test", "resources");
//        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
//
//        System.out.println(absolutePath);
//
//        assertTrue(absolutePath.endsWith("src/test/resources"));
//    }
//
//    @Test
//    public void testReadFileWithClassLoader(){
//        ClassLoader classLoader = this.getClass().getClassLoader();
//        File file = new File(classLoader.getResource("lorem_ipsum.txt").getFile());
//        assertTrue(file.exists());
//
//    }
//
//    @Test
//    public void testReadFileWithResource() {
//        URL url = this.getClass().getResource("/lorem_ipsum.txt");
//        File file = new File(url.getFile());
//        assertTrue(file.exists());
//    }

    @Test
    public void readFileRelativePath_DSV_Input_1() {
        File file = new File(dsvInputOneFilePath);
        assertTrue(file.exists());
    }

    @Test
    public void readFileRelativePath_DSV_Input_2() {
        File file = new File(dsvInputTwoFilePath);
        assertTrue(file.exists());
    }
}