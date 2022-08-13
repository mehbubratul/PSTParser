# PSTParser

A simple file parser using Java.

## How to run:
-  Call from console where the jar is located.
  - Here inside target folder the jar **pstparser-jar-with-dependencies.jar** is resided.
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
- **StandardCharsets.UTF_8** is used.
- **jUnit** is used for test case.

## TODO : [Done]
- Stream API Incorporation (Incorporated)
  - In main method: **doFileProcessUsingStream** is added
  - In main method: **doFileProcess** is deprecated using **@Deprecated**.

## Test Data Format :

### Input : 

- Sample #1

![image](https://user-images.githubusercontent.com/75577090/184492655-2b62e202-3d34-4475-9292-f013c8747803.png)

firstName|middleName|lastName|gender|dateOfBirth|salary
Wolfgang|Amadeus|Mozart|Male|1756-01-27|1000
Albert||Einstein|Male|1955/04/18|2000
Marie, Salomea|"Skłodowska |"|Curie|Female|04-07-1934|3000

- Sample #2

![image](https://user-images.githubusercontent.com/75577090/184492720-6746f6d2-f4f7-40e0-9510-a81b1c69577d.png)

firstName,middleName,lastName,gender,dateOfBirth,salary
Wolfgang,AmadeusMozart,Male1756-01-27,1000
Albert,,Einstein,Male1955/04/18,2000
"Marie, Salomea",Skłodowska |,Curie,Female,04-07-1934,3000


### Output :

{"firstName":"Wolfgang","middleName":"Amadeus","lastName":"Mozart","gender":"Male","dateOfBirth":"1756-01-27","salary":"1000"}
{"firstName":"Albert","lastName":"Einstein","gender":"Male","dateOfBirth":"1955-04-18","salary":"2000"}
{"firstName":"Marie, Salomea","middleName":"Skłodowska |","lastName":"Curie","gender":"Female","dateOfBirth":"1934-07-04","salary":"3000"}


## Improvement Options:
- Dynamically, define separator from data.
- Currently, few date patterns is used (see below). This can be improved as required.
  - Date Patterns : "yyyy-MM-dd", "yyyy/MM/dd", "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "MM-dd-yyyy", "MM/dd/yyyy", "yyyyMMdd"

