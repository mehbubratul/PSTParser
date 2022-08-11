package com.mehbub.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehbub.lib.DSV;
import com.mehbub.lib.DateUtil;
import com.mehbub.lib.FileUtil;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class FileParser {
    final String inputFilePath;
    final char separator;

    public FileParser(String inputFilePath, char separator) {
        this.inputFilePath = inputFilePath;
        this.separator = separator;
    }

    public AbstractMap.SimpleEntry<Boolean, String> parseFile() {
//        try (BufferedInputStream inputStream =
//                     new BufferedInputStream(
//                             new FileInputStream(new File(inputFilePath)))){

        //region File Validation

        FileUtil fileUtil = new FileUtil(inputFilePath);

        AbstractMap.SimpleEntry<Boolean, String> result = fileUtil.fileValidation();

        if (!result.getKey()) {
            return new AbstractMap.SimpleEntry<>(false, result.getValue());
        }

        //endregion

        try (FileInputStream inputStream = new FileInputStream(inputFilePath)) {

            // FileInputStream = 13 sec 17 ms
            // BufferedInputStream = 13 sec 117 ms

            // System.out.println("Remaining bytes that can be read : " + inputStream.available());
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
            return new AbstractMap.SimpleEntry<>(true, "File parsed successfully.");
            //endregion

        } catch (Exception ex) {
            return new AbstractMap.SimpleEntry<>(false, ex.getMessage());
        }
    }
}
