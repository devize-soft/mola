package com.mola.charts.util;

import java.io.*;

/**
 * <b>Author: Ronald Grant </b> <br>
 * This file parses the image histogram data into a normalized arff format. File
 * does one category at a time. Output has to be manually copied and pasted into
 * arff file.<br>
 * <p>
 * Usage: This program is designed specifically for parsing tick data obtained
 * from http://hopey.netfonds.no <br>
 * Tick data can be downloaded from the following link: <br>
 * http://hopey.netfonds.no/tradedump.php?date=201212/05&paper=GOOG.O&csv_format
 * =txt <br>
 * <br>
 * The link above is for Google Nasdaq: GOOG for the date 12-05-2012 <br>
 * Data for the last 5 trading days is usually available for Nasdaq stocks. <br>
 * All tick files should be saved as .txt files in a folder specifically
 * designated to hold the tick data files.<br>
 * It is recommended that the folder does not contain any other files, other
 * than the txt files to be parsed.
 * </p>
 */

public class ArffGenerator {
    private String inputDirectory;

    private StringBuilder outfileName;

    private StringBuilder testOutfileName;

    private FileReader fr = null;

    private BufferedReader dis = null;

    private File outputFile;

    private File testOutputFile;

    private FileOutputStream fos;

    private FileOutputStream testFos;

    private OutputStreamWriter out;

    private OutputStreamWriter testOut;

    private boolean randomValues;

    public void generate() throws IOException {

        File directory = new File(inputDirectory);
        File[] files = directory.listFiles(new Filter());
        for (File file : files) {
            System.out.print("Generating Arff files for: " + file.getName()
                    + "...");
            printHeader(file.getName());
            fr = new FileReader(file);
            dis = new BufferedReader(fr);
            String line;
            StringBuilder arffData = new StringBuilder();
            StringBuilder arffTestData = new StringBuilder();
            dis.readLine(); // get rid of header
            String[] dataArray;
            while ((line = dis.readLine()) != null) {
                dataArray = line.replaceAll("\\t", ",").replaceAll(",,,", "")
                        .split(",");
                for (int i = 0; i < 3; ++i) {
                    if (i == 0) {
                        String year = dataArray[i].substring(0, 4);
                        String month = dataArray[i].substring(4, 6);
                        String day = dataArray[i].substring(6, 8);
                        String hour = dataArray[i].substring(9, 11);
                        String minute = dataArray[i].substring(11, 13);
                        String seconds = dataArray[i].substring(13, 15);
                        StringBuilder dateString = new StringBuilder(year + "-"
                                + month + "-" + day + " ");
                        dateString.append(hour + ":" + minute + ":" + seconds);
                        arffData.append("\"" + dateString.toString() + "\""
                                + ",");
                        arffTestData.append("\"" + dateString.toString() + "\""
                                + ",");
                    }
                    if (i > 0 && i < 2) {
                        arffData.append(dataArray[i] + ",");

                        if (randomValues) {

                            if (Math.random() * 3 < 1.5) {
                                arffTestData.append(dataArray[i] + ",");
                            } else {
                                arffTestData.append("?,");
                            }
                        } else {
                            arffTestData.append("?,");
                        }
                    } else if (i >= 2) {
                        arffData.append(dataArray[i] + "\n");

                        if (randomValues) {
                            if (Math.random() * 2 < 1) {
                                arffTestData.append(dataArray[i] + "\n");
                            } else {
                                arffTestData.append("?\n");
                            }
                        } else {
                            arffTestData.append("?\n");
                        }
                    }
                }
            }
            out.write(arffData.toString());
            out.flush();
            testOut.write(arffTestData.toString());
            testOut.flush();
            System.out.print("Done\n");
        }
    }

    public void printHeader(String filename) throws IOException {
        outfileName = new StringBuilder(filename);
        testOutfileName = new StringBuilder(filename);
        testOutfileName.replace(outfileName.indexOf(".txt"),
                outfileName.length(), "-Test.arff");
        outfileName.replace(outfileName.indexOf(".txt"), outfileName.length(),
                ".arff");

        outputFile = new File(outfileName.toString());
        testOutputFile = new File(testOutfileName.toString());
        fos = new FileOutputStream(outputFile);
        testFos = new FileOutputStream(testOutputFile);
        testOut = new OutputStreamWriter(testFos, "UTF-8");
        out = new OutputStreamWriter(fos, "UTF-8");

        out.write("@relation " + outfileName + "\n\n");
        out.write("@attribute time DATE \"yyyy-MM-dd HH:mm:ss\" \n@attribute price REAL\n@attribute quantity NUMERIC\n\n@data\n");

        testOut.write("@relation " + testOutfileName + "\n\n");
        testOut.write("@attribute time DATE \"yyyy-MM-dd HH:mm:ss\" \n@attribute price REAL\n@attribute quantity NUMERIC\n\n@data\n");
    }

    public void generateTestFiles() {

    }

    public String getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

}

class Filter implements FileFilter {
    public boolean accept(File file) {
        return file.getName().endsWith("txt");
    }
}
