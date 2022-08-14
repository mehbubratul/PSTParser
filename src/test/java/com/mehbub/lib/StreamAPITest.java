package com.mehbub.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * https://reflectoring.io/processing-files-using-java-8-streams/
 * Streams are lazy-loaded
 * (i.e. they generate elements upon request instead of storing them all in memory),
 * reading and processing files will be efficient in terms of memory used.
 */

public class StreamAPITest {

    //File dsvInputTwoFile;
    String dsvInputTwoFilePath;
    Pattern pattern = Pattern.compile("|");

    @BeforeEach
    void setUp() {
        dsvInputTwoFilePath = "src/test/resources/testData/DSV input 2.txt";
        //dsvInputTwoFile = new File("src/test/resources/testData/DSV input 2.txt");
    }

    private List<String> getHeaderList() {
        List<String> innerList = new ArrayList<>();
        innerList.add("firstName");
        innerList.add("middleName");
        innerList.add("lastName");
        innerList.add("gender");
        innerList.add("dateOfBirth");
        innerList.add("salary");
        return innerList;
    }

    //region Load Files using File.lines() , Files.newBufferedReader() , Files.readAllLines()

    /**
     * The lines() method takes the Path representing the file as an argument.
     * This method does not read all lines into a List,
     * but instead populates lazily as the stream is consumed and this allows efficient use of memory.
     */
    @Test
    void loadFileLinesUsingNioFilesLines() {
        Stream<String> lines = null;
        try {
            lines = Files.lines(Path.of(dsvInputTwoFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lines.forEach(System.out::println);
    }

    /**
     * Same as loadFileLinesUsingNioFilesLines
     * Additionally, try-with-resources used to close any resource that implements either AutoCloseable or Closeable.
     * The streams will now be automatically closed when the try block is exited
     */
    @Test
    void loadFileLinesUsingNioFilesLinesAndTryWithResources() {
        try (Stream<String> lines = Files
                .lines(Path.of(dsvInputTwoFilePath), StandardCharsets.UTF_8)) {
            lines.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Same results of using Files.lines() can be achieved
     * by invoking the lines() method on BufferedReader also
     */
    @Test
    void loadFileLinesUsingBufferedReader() {
        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(Paths.get(dsvInputTwoFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stream<String> lines = br.lines();
        lines.forEach(System.out::println);
    }

    /**
     * Same as loadFileLinesUsingBufferedReader
     * Additionally, try-with-resources used to close any resource that implements either AutoCloseable or Closeable.
     * The streams will now be automatically closed when the try block is exited
     */
    @Test
    void loadFileLinesUsingBufferedReaderAndTryWithResources() {
        try (Stream<String> lines =
                     (Files.newBufferedReader(Paths.get(dsvInputTwoFilePath), StandardCharsets.UTF_8)
                             .lines())) {
            lines.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method loads the entire contents of the file in one go
     * and hence is not memory efficient like the Files.lines() method
     */
    @Test
    void loadFileLinesUsingNioFilesReadAllLines() {
        try {
            List<String> strList = Files
                    .readAllLines(Path.of(dsvInputTwoFilePath), StandardCharsets.UTF_8);
            strList.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    @Test
    void givenRegexWithSeparatorEscaped_whenSplitStr_thenSplits() {
        //String line = "foo,bar,c;qual=\"baz,blurb\",d;junk=\"quux,syzygy\"";
        String line = "Marie, Salomea|\"Skłodowska |\"|Curie|Female|04-07-1934|3000";
        Pattern p = Pattern.compile("\\G\"(.*?)\",?|([^|]*),?");

        String[] a = p.matcher(line).results()
                .map(m -> m.group(m.start(1) < 0 ? 2 : 1))
                .toArray(String[]::new);
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }

    @Test
    void testLineItemParsing() {
        //String input = "foo,bar,c;qual=\"baz,blurb\",d;junk=\"quux,syzygy\"";
        // String line = "Wolfgang|Amadeus|Mozart|Male|1756-01-27|1000";
        // String line = "Albert||Einstein|Male|1955/04/18|2000";
        String line = "Marie, Salomea|\"Skłodowska |\"|Curie|Female|04-07-1934|3000";
        //line = "\"Skłodowska |\"";
        // String line = "Wolfgang,Amadeus,রাতুল ,Male,তারিখ ,1000";
        // String line = "Albert,,Einstein,Male,তারিখ ,2000";
        //String line = "\"Marie, Salomea\",Skłodowska |,Curie,Female,তারিখ ,3000";
        char separator = '|';

        List<String> result = new ArrayList<String>();

        int start = 0;
        boolean isQuotePresent = false;
        boolean inQuotes = false;
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

        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
    }

    @Test
    void testListParsing() throws IOException {

        List<String> lines = new ArrayList<>();

        lines.add("Wolfgang|Amadeus|Mozart|Male|1756-01-27|1000");
        lines.add("Albert||Einstein|Male|1955/04/18|2000");
        lines.add("Marie, Salomea|\"Skłodowska |\"|Curie|Female|04-07-1934|3000");

        List<String> headerFields = getHeaderList();

        ObjectMapper mapper = new ObjectMapper();
        FileUtil fileUtil = new FileUtil(dsvInputTwoFilePath);
        FileWriter fileWriter = new FileWriter(fileUtil.getOutFilePath(), StandardCharsets.UTF_8);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        Character separator = '|';

        lines.stream()
                .map(String::toString)
                .map(s -> getSplitStringList(s, separator)).toList()
                .forEach(lineItems -> {
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
                });
        printWriter.close();
        fileWriter.close();
    }

    private List<String> getSplitStringList(String line, char separator) {

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
