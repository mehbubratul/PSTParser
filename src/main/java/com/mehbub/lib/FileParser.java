package com.mehbub.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileParser {

    //region private fields & constructor
    private final String inputFilePath;
    private final char separator;
    private final Charset charset;

    public FileParser(String inputFilePath, char separator) {
        this.inputFilePath = inputFilePath;
        this.separator = separator;
        this.charset = StandardCharsets.UTF_8;
    }
    //endregion

    //region public methods

    @Deprecated
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

    @Deprecated
    public AbstractMap.SimpleEntry<Boolean, String> parseFileUsingStream() {

        //region File Validation

        FileUtil fileUtil = new FileUtil(inputFilePath);

        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();

        if (!result.getKey()) {
            return new AbstractMap.SimpleEntry<>(false, result.getValue());
        }

        //endregion

        try (Stream<String> linesUnfiltered = Files.lines(Path.of(inputFilePath), charset)) {

            Iterator<String> it = linesUnfiltered.iterator();

            //region headers

            String firstLine = it.next();

            List<String> headerFields = getHeaderList(firstLine);

            if (headerFields == null) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }
            if (headerFields.size() == 0) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }

            //endregion

            //region Rest Lines

            List<String> collectedOtherLines = getRestLines(it);

            if (collectedOtherLines.size() == 0) {
                return new AbstractMap.SimpleEntry<>(false, "Only headers are present in text file.");
            }

            //endregion

            //region Instantiating FileWriter, PrintWriter & ObjectMapper

            FileWriter fileWriter = new FileWriter(fileUtil.getOutFilePath(), charset);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            ObjectMapper mapper = new ObjectMapper();

            //endregion

            for (int index = 0; index < collectedOtherLines.size(); index++) {

                //region Get tokens/Words into list from each line item
                List<String> splitStringList = getSplitStringList(collectedOtherLines.get(index));

                if (headerFields.size() > splitStringList.size()) {
                    return new AbstractMap.SimpleEntry<>(false, "Mismatch in header count with value provided in line # " + (index + 1));
                }
                //endregion

                //region Creating Map using header as key and split string as value

                Map<String, Object> obj = new LinkedHashMap<>();

                for (int i = 0; i < headerFields.size(); i++) {

                    Object value = splitStringList.get(i);

                    if (value == null || value == "") {
                        continue;
                    }

                    AbstractMap.SimpleEntry<Boolean, String> entry = DateUtil.DateValidation(String.valueOf(splitStringList.get(i)));

                    obj.put(headerFields.get(i), entry.getKey() ? entry.getValue() : value);

                }

                //endregion

                //region writing transformed line item in out file

                printWriter.println(mapper.writeValueAsString(obj));

                //endregion

            }

            //region Closing FileWriter & PrintWriter

            printWriter.close();
            fileWriter.close();

            //endregion

            return new AbstractMap.SimpleEntry<>(true, "File parsed successfully. Please, check the jar / input file location to get the JSONL file.");

        } catch (Exception ex) {
            return new AbstractMap.SimpleEntry<>(false, "Error: Unable to parse file..." + ex.getMessage());
        }

    }

    public AbstractMap.SimpleEntry<Boolean, String> parseFileUsingStreamSupport() {

        //region File Validation

        FileUtil fileUtil = new FileUtil(inputFilePath);

        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();

        if (!result.getKey()) {
            return new AbstractMap.SimpleEntry<>(false, result.getValue());
        }

        //endregion

        try (Stream<String> linesUnfiltered = Files.lines(Path.of(inputFilePath), charset)) {

            Iterator<String> it = linesUnfiltered.iterator();

            //region Headers

            String firstLine = it.next();

            boolean nullOrEmptyOrBlank = new StringUtil().isNullOrEmptyOrBlank(firstLine);

            if (nullOrEmptyOrBlank) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }

            List<String> headerFields = getHeaderList(firstLine);

            if (headerFields == null) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }
            if (headerFields.size() == 0) {
                return new AbstractMap.SimpleEntry<>(false, "Headers must be present in text file.");
            }

            //endregion

            //region Rest Lines

            ObjectMapper mapper = new ObjectMapper();
            FileWriter fileWriter = new FileWriter(fileUtil.getOutFilePath(), charset);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(it, 0), false)
                    .map(String::toString)
                    .map(line -> getSplitStringList(line)).toList()
                    .forEach(lineItems -> {
                        if (lineItems == null) {
                            return;
                        }
                        Map<String, Object> obj = new LinkedHashMap<>();

                        for (int index = 0; index < headerFields.size(); index++) {
                            Object value = lineItems.get(index);
                            if (value == null || value == "") {
                                continue;
                            }
                            String key = headerFields.get(index);
                            AbstractMap.SimpleEntry<Boolean, String> entry = DateUtil.DateValidation(String.valueOf(value));
                            obj.put(key, entry.getKey() ? entry.getValue() : value);
                        }

                        try {
                            printWriter.println(mapper.writeValueAsString(obj));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
            ;

            printWriter.close();
            fileWriter.close();


            //endregion

            return new AbstractMap.SimpleEntry<>(true, "File parsed successfully. Please, check the jar / input file location to get the JSONL file.");

        } catch (Exception ex) {
            return new AbstractMap.SimpleEntry<>(false, "Error: Unable to parse file..." + ex.getMessage());
        }

    }

    //endregion

    //region private methods

    private List<String> getRestLines(Iterator<String> it) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(it, 0), false)
                .map(String::toString)
                .collect(Collectors.toList());
    }

    private List<String> getHeaderList(String line) {

        List<String> headerFields = Stream.of(line)
                .flatMap(Pattern.compile(Pattern.quote(String.valueOf(separator)))::splitAsStream)
                .collect(Collectors.toList());

        return headerFields;
    }

    private List<String> getSplitStringList(String line) {

        List<String> result = new ArrayList<>();

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

    //endregion

}
