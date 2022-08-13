package com.mehbub.lib;

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
    private final String inputFilePath;
    private final char separator;
    private final Charset charset;

    public FileParser(String inputFilePath, char separator) {
        this.inputFilePath = inputFilePath;
        this.separator = separator;
        this.charset = StandardCharsets.UTF_8;
    }

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

            System.out.println(" File parsed successfully.");
            System.out.println(" Please, check the jar / input file location to get the JSONL file.");

            return new AbstractMap.SimpleEntry<>(true, "File parsed successfully.");

        } catch (Exception ex) {
            return new AbstractMap.SimpleEntry<>(false, ex.getMessage());
        }

    }

    private List<String> getRestLines(Iterator<String> it) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(it, 0), false)
                .map(String::toString)
                .collect(Collectors.toList());
    }

    private List<String> getHeaderList(String firstLine) {
        List<String> lists = Stream.of(firstLine).collect(Collectors.toList());
        return Stream.of(lists)
                .flatMap(Collection::stream)
                .flatMap(Pattern.compile(Pattern.quote(String.valueOf(separator)))::splitAsStream)
                .collect(Collectors.toList());
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
}
