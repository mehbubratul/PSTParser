# PSTParser

A simple file parser using Java.

## How to run:
-  Call from console where the jar is located.
-  pattern : java -jar jarname -f filename -s separator
-  example : **java -jar pstparser-jar-with-dependencies.jar -f "E:\\Java\\fileToParse.txt" -s "|"**

## Scope:
- This is a maven project.This is built using "**mvn package**" command.
- This converts delimiter separator files into JSONL format file
- Test Datas are inside **..\src\test\resources\testData** folder.
- Output file will be created in the jar location.
- **Comma (,) and pipe (|)** is tested as delimiter.
- If any data can be parsed with date format (defined date patterns) then that will be convert to date **"YYYY-MM-dd"** format
- FileInputStream is used to read the file content.
- First line of the file will be used as header.
- If first line is empty, then system will not parse the file.
- System expects **file name and separator** from the arguments as required. 
- **jUnit** is used for test case.

## TODO
- Stream API Incorporation
## Improvemnt Options:
- Dyncamically, define separator from data.

