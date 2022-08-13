package com.mehbub.lib;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileParser {
    final String inputFilePath;
    final char separator;

    public FileParser(String inputFilePath, char separator) {
        this.inputFilePath = inputFilePath;
        this.separator = separator;
    }

    public AbstractMap.SimpleEntry<Boolean, String> parseFile() {

        //region File Validation

        FileUtil fileUtil = new FileUtil(inputFilePath);

        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();

        if (!result.getKey()) {
            return new AbstractMap.SimpleEntry<>(false, result.getValue());
        }

        //endregion

        //region parsing

        try (FileInputStream inputStream = new FileInputStream(inputFilePath)) {

            DSV DSV = new DSV(false, separator, inputStream);

            //region headers
            List<?> headerFields = null;
            if (DSV.hasHeaders()) {
                List<Object> headers = DSV.headers();
                headerFields = new ArrayList<>(headers);
            }

            if (headerFields == null) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }
            if (headerFields.size() == 0) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }
            //endregion

            //region records

            String outFilePath = fileUtil.getOutFilePath();
            FileWriter fileWriter = new FileWriter(outFilePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            ObjectMapper mapper = new ObjectMapper();

            int lineNumber = 1;
            while (DSV.hasNext()) {

                List<?> cells = DSV.next();

                lineNumber = lineNumber + 1;

                if (headerFields.size() > cells.size()) {
                    return new AbstractMap.SimpleEntry<>(false, "Mismatch in header count with value provided in line # " + lineNumber);
                }

                Map<Object, Object> obj = new LinkedHashMap<>();

                for (int i = 0; i < headerFields.size(); i++) {
                    String key = (String) headerFields.get(i);
                    Object value = cells.get(i);
                    if (value == null || value == "") {
                        continue;
                    }

                    AbstractMap.SimpleEntry<Boolean, String> entry = DateUtil.DateValidation(String.valueOf(cells.get(i)));

                    obj.put(key, entry.getKey() ? entry.getValue() : value);

                }
                printWriter.println(mapper.writeValueAsString(obj));
            }

            printWriter.close();
            fileWriter.close();

            System.out.println("... File parsed successfully. ...");
            System.out.println("Please, check the jar location to get the produced file.");
            return new AbstractMap.SimpleEntry<>(true, "File parsed successfully.");
            //endregion

        } catch (Exception ex) {
            return new AbstractMap.SimpleEntry<>(false, ex.getMessage());
        }

        //endregion
    }

    public AbstractMap.SimpleEntry<Boolean, String> parseFileUsingStream() {

        //region File Validation

        FileUtil fileUtil = new FileUtil(inputFilePath);

        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();

        if (!result.getKey()) {
            return new AbstractMap.SimpleEntry<>(false, result.getValue());
        }

        //endregion

        try (Stream<String> linesUnfiltered = Files.lines(Path.of(inputFilePath), StandardCharsets.UTF_8)) {

            Iterator<String> it = linesUnfiltered.iterator();

            //region headers

            String firstLine = it.next();
            List<String> headers = Stream.of(firstLine).collect(Collectors.toList());
            List<String> headerFields = getWordList(headers);
            if (headerFields == null) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }
            if (headerFields.size() == 0) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }
            //System.out.println(headerFields);

            //endregion

            //region Rest Lines

            List<String> collectedOtherLines = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(it, 0), false)
                    .map(String::toString)
                    .collect(Collectors.toList());
            /*
            Stream<String> otherLines = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(it, 0), false);
            List<String> collect = otherLines
                    .map(String::toString)
                    .collect(Collectors.toList());
            // String outFilePath = fileUtil.getOutFilePath();
            */

            FileWriter fileWriter = new FileWriter(fileUtil.getOutFilePath());
            PrintWriter printWriter = new PrintWriter(fileWriter);
            ObjectMapper mapper = new ObjectMapper();

            for (int index = 0; index < collectedOtherLines.size(); index++) {

                List<String> splitStringList = getSplittedStringList(collectedOtherLines.get(index));

                if (headerFields.size() > splitStringList.size()) {

                    return new AbstractMap.SimpleEntry<>(false, "Mismatch in header count with value provided in line # " + (index + 1));

                }

                Map<String, Object> obj = new LinkedHashMap<>();

                for (int i = 0; i < headerFields.size(); i++) {

                    Object value = splitStringList.get(i);

                    if (value == null || value == "") {
                        continue;
                    }

                    AbstractMap.SimpleEntry<Boolean, String> entry = DateUtil.DateValidation(String.valueOf(splitStringList.get(i)));

                    obj.put(headerFields.get(i), entry.getKey() ? entry.getValue() : value);
                }

                printWriter.println(mapper.writeValueAsString(obj));

            }
            //endregion

            //region Closing FileWriter & PrintWriter
            printWriter.close();
            fileWriter.close();
            //endregion

            System.out.println("... File parsed successfully. ...");
            System.out.println("Please, check the jar location to get the produced file.");
            return new AbstractMap.SimpleEntry<>(true, "File parsed successfully.");

        } catch (Exception ex) {
            return new AbstractMap.SimpleEntry<>(false, ex.getMessage());
        }

    }

    private List<String> getWordList(List<String>... lists) {
        List<String> collect = Stream.of(lists)
                .flatMap(Collection::stream)
                .flatMap(Pattern.compile(Pattern.quote(String.valueOf(separator)))::splitAsStream)
                .collect(Collectors.toList());
        return collect;
    }

    private List<String> getSplittedStringList(String line) {
        List<String> result = new ArrayList<String>();

        int start = 0;
        boolean inQuotes = false;
        boolean isQuotePresent = false;
        for (int current = 0; current < line.length(); current++) {
            if (line.charAt(current) == '"') {
                inQuotes = !inQuotes; // toggle state
                isQuotePresent = true;
            } else {
                if (line.charAt(current) == separator && !inQuotes) {
                    if (isQuotePresent) {
                        result.add(line.substring(start + 1, current - 1));
                        isQuotePresent = false;
                    } else {
                        result.add(line.substring(start, current));
                    }
                    start = current + 1;
                }
            }
        }
        result.add(line.substring(start));

        return result;
    }
}
