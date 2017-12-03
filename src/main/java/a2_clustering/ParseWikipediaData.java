package a2_clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParseWikipediaData {
    private HashSet<String> allWords = new HashSet<>(); // 19,398 original size w/o FilterWords()
    private HashMap<String, HashMap<String, Integer>> wordPages = new HashMap<>();
    private String entryPage = "not set";
    private String filePath;
    private String wordBagsPath = System.getProperty("user.dir") + "/data/Crawler/Words";
    private String parsedDataPath = "/ParsedWikipedia";

    public ParseWikipediaData(String pages) {
        entryPage = pages;
        filePath = FileSysDB.matchingDataFileFor(pages, parsedDataPath);

        if (!(filePath != null && !filePath.equals("not found"))) {
            for (String page : pages.split(",")) {
                parseEachPage(wordBagsPath + "/" + page);
            }

            FilterWords();
            String lines = DataToStringLines();
            filePath = FileSysDB.PrintLinesToFile(lines, entryPage, parsedDataPath); // overwrites "not found" by creating one and returning its path string
        }
    }

    private void parseEachPage(String wordDataPath) {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(wordDataPath))) {

            for (Path entry : stream) {
                CountWordBag(entry);
            }

        } catch (Exception e) {
            System.err.print(e);
            e.printStackTrace();
        }

    }

    public String dataFileLocation() { return filePath; }

    private void CountWordBag(Path path) throws IOException {
        BufferedReader br = new FileOpener(path).br;
        HashMap<String, Integer> wordCount = new HashMap<>();

        // todo lowercase the bag of words in crawler. remove punctuation. pure words.
        String line = br.readLine();
        while (line != null) {
            String[] splittedWordBag = line.split(" ");

            for (String word : splittedWordBag) {
                Integer count = wordCount.putIfAbsent(word, 1); // returns null if a count wasnt already set the value if already set
                if (count != null) {
                    count++;
                    wordCount.put(word, count);
                    allWords.add(word);
                }
            }

            line = br.readLine();
        }
        wordPages.put(FileSysDB.lastInPath(path.toString()), wordCount);
    }

    private void FilterWords() {
        Integer totalPages = wordPages.size();
        System.out.println("Words before filter: " + allWords.size());
        ArrayList<String> toRemove = new ArrayList<>();

        allWords.forEach(word -> {
            final int[] count = {0};

            wordPages.forEach((pageName, wordCounts) -> {
                Integer wc = wordCounts.get(word);
                if (wc != null) count[0]++;
            });

            int appersIn = count[0] - totalPages;
            double fraction = (double) count[0] / totalPages;
            if (!(fraction > 0.1 && fraction < 0.5)) toRemove.add(word);

        });

        for (String w : toRemove) {
            allWords.remove(w);
        }

        System.out.println("Words after filter: " + allWords.size());

    }

    private String DataToStringLines() {
        StringBuilder wholeFile = new StringBuilder();
        StringBuilder wordLine = new StringBuilder();
        wordLine.append("Word\t");
        ArrayList<String> pageLines = new ArrayList<>();

        allWords.forEach(word -> {
            wordLine.append(word).append("\t");
        });

        wordLine.append("\n");
        wholeFile.append(wordLine.toString());

        wordPages.forEach((pageName, wordCounts) -> {
            StringBuilder pageLine = new StringBuilder();
            pageLine.append(pageName).append("\t");

            allWords.forEach(word -> {
                Integer count = wordCounts.get(word);
                if (count == null) count = 0;
                pageLine.append(count.toString()).append("\t");

            });

            wholeFile.append(pageLine.toString()).append("\n");
        });

        return wholeFile.toString();
    }

}
