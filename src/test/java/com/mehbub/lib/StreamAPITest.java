package com.mehbub.lib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    // https://stackoverflow.com/questions/60111797/java-8-streams-filter-lines-from-csv-but-retain-first-line
    @Test
    void test2() {
        try (Stream<String> linesUnfiltered = Files.lines(Path.of(dsvInputTwoFilePath), StandardCharsets.UTF_8)) {
            Iterator<String> it = linesUnfiltered.iterator();

            // headers
            String firstLine = it.next();
            List<String> headers = Stream.of(firstLine).collect(Collectors.toList());
            List<String> wordList = getWordList(headers);
            //System.out.println(wordList);

            // other lines
            Stream<String> otherLines = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(it, 0), false);
            List<String> collect = otherLines
                    .map(String::toString)
                    .collect(Collectors.toList());
            Character separator = '|';
            //collect.stream().forEach(line -> getSplittedStringList(line, separator));

            for (int index = 0; index < collect.size(); index++) {
                List<String> splittedStringList = getSplittedStringList(collect.get(index), separator);
                System.out.println(splittedStringList);
            }
//            List<String> otherLineList = getWordList(collect);
//            for (int i = 0; i < otherLineList.size(); i++) {
//                System.out.println(otherLineList.get(i));
//            }
            //System.out.println(otherLineList);

//            Stream<String> linesFiltered = null;//otherLines.map(s -> s.split(Pattern.quote("|"))).;
//            Stream<String> result = Stream.concat(Stream.of(firstLine), otherLines);
//            Files.write(Path.of(dsvInputTwoFilePath), (Iterable<String>) result::iterator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> getSplittedStringList(String line, Character separator) {
        List<String> result = new ArrayList<String>();

        int start = 0;
        boolean inQuotes = false;
        for (int current = 0; current < line.length(); current++) {
            if (line.charAt(current) == '\"') inQuotes = !inQuotes; // toggle state
            else {
                if (line.charAt(current) == separator && !inQuotes) {
                    result.add(line.substring(start, current));
                    start = current + 1;
                }
            }
        }
        result.add(line.substring(start));

        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
        return result;
    }

    private List<String> getWordList(List<String>... lists) {

        List<String> collect = Stream.of(lists) // Stream<Stream<String>>
                .flatMap(Collection::stream) // Stream<String>
                //.flatMap(Pattern.compile("\\P{L}")::splitAsStream) //Stream<String>
                .flatMap(Pattern.compile(Pattern.quote("|"))::splitAsStream) //Stream<String>
                .collect(Collectors.toList());// List<String>
        return collect;
    }


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
    void anotherTest() {
        //String line = "foo,bar,c;qual=\"baz,blurb\",d;junk=\"quux,syzygy\"";
        String line2 = "Marie, Salomea|\"Skłodowska |\"|Curie|Female|04-07-1934|3000";
        String otherThanQuote = " [^\"] ";
        String quotedString = String.format(" \" %s* \" ", otherThanQuote);
        String regex = String.format("(?x) " + // enable comments, ignore white spaces
                        "|                         " + // match a comma
                        "(?=                       " + // start positive look ahead
                        "  (?:                     " + //   start non-capturing group 1
                        "    %s*                   " + //     match 'otherThanQuote' zero or more times
                        "    %s                    " + //     match 'quotedString'
                        "  )*                      " + //   end group 1 and repeat it zero or more times
                        "  %s*                     " + //   match 'otherThanQuote'
                        "  $                       " + // match the end of the string
                        ")                         ", // stop positive look ahead
                otherThanQuote, quotedString, otherThanQuote);

        String[] tokens = line2.split(regex, -1);
        for (String t : tokens) {
            System.out.println("> " + t);
        }
    }

    @Test
    void anotherTest2() {
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
}
