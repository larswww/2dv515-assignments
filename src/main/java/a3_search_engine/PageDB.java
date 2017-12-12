package a3_search_engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class PageDB {
    HashMap<String, Integer> wordToId = new HashMap<>();
    ArrayList<Page> pages = new ArrayList<>();

    public int getIdForWord(String word) {
        if (wordToId.containsKey(word)) {
            return wordToId.get(word);
        } else {
            int id = wordToId.size();
            wordToId.put(word, id);
            return id;
        }
    }

    public ArrayList<Page> pages() { return this.pages; }

    public void generatePage(String url, File wordsFile, File linksFile) {

        try {
            ArrayList<Integer> words = new ArrayList<>();

            BufferedReader win = new BufferedReader(new FileReader(wordsFile));
            String line;
            while ((line = win.readLine()) != null) {
                String[] wlist = line.split(" ");
                for (String w : wlist) {
                    int id = this.getIdForWord(w);
                    words.add(id);
                }
            }
            win.close();

            // add the links

            String allLinks = Files.lines(linksFile.toPath(), StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
            ArrayList<String> links = new ArrayList<>(Arrays.asList(allLinks.split("\n")));

            Page page = new Page(url, words, links);
            pages.add(page);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void query(String query) {
        ArrayList<SearchResult> result = new ArrayList<>();

        double[] content  = new double[this.pages().size()];
        double[] location = new double[this.pages().size()];
        double[] distance = new double[this.pages().size()];

        for (int i = 0; i < this.pages().size(); i ++) {
            Page page = this.pages().get(i);
            content[i] = getCountFrequencyScore(page, query);
            location[i] = getCountLocationScore(page, query);
            distance[i] = getWordDistanceScore(page, query);
        }

        normalizeScores(content, false);
        normalizeScores(location, true);
        normalizeScores(distance, true);

        for (int i = 0; i < this.pages().size(); i++) {
            Page p = this.pages().get(i);
            double score = (1.0 * content[i]) + (1.0 * p.pageRank) + (0.5 * location[i]) + (0.3 * distance[i]);
            result.add(new SearchResult(p, score));
        }

        Collections.sort(result);

        System.out.println("\n Result for search '" + query + "':");
        int no = 10;
        for (int i = 0; i < no; i++) {
            System.out.println(result.get(i).toString());
        }

    }

    public double getCountFrequencyScore(Page page, String query) {
        double score = 0.0;
        ArrayList<Integer> pageWords = page.words();
        String[] queryWords = query.split(" ");

        for (String word : queryWords) {
            int wid = getIdForWord(word);

            for (Integer i : pageWords) {
                if (i == wid) score += 1;

            }

        }
        return score;
    }

    public double getCountLocationScore(Page page, String query) {
        double score = 0.0;
        ArrayList<Integer> pageWords = page.words();
        String[] queryWords = query.split(" ");

        for (String word : queryWords) {
            int wid = getIdForWord(word);

            boolean notFound = true;
            for (int i = 0; i < pageWords.size(); i++) {

                if (wid == pageWords.get(i)) {
                    notFound = false;
                    score += i;
                }

            }

            if (notFound) score += 100000;
        }

        return score;
    }

    public double getWordDistanceScore(Page page, String query) {
        double score = 0.0;
        ArrayList<Integer> pageWords = page.words();
        String[] queryWords = query.split(" ");
        Double[] wordDistances = new Double[queryWords.length];

        for (int i = 0; i < queryWords.length; i++) {
            wordDistances[i] = getCountLocationScore(page, queryWords[i]);
        }

        for (Double locationScore : wordDistances) {
            if (locationScore > 999999) {
                score += locationScore;
            } else {
                score -= locationScore;
            }

        }



        return score;
    }

    public void normalizeScores(double[] scores, boolean smallIsBetter) {

        if (smallIsBetter) {
            double min = Double.MAX_VALUE;

            for (double s : scores) {
                if (s < min) {
                    min = s;
                }
            }

            for (int i = 0; i < scores.length; i++) {
                scores[i] = min / Math.max(scores[i], 0.0001);
            }

        } else {
            double max = Double.MIN_VALUE;

            for (double s : scores) {
                if (s > max) {
                    max = s;
                }
            }

            if (max == 0.0) max = 0.00001;
            for (int i = 0; i < scores.length; i++) {
                scores[i] = scores[i] / max;
            }
        }
    }

    public void calculatePageRank() {
        for (int i = 0; i < 20; i++) {

            for (Page p : this.pages) {
             iteratePageRank(p);
            }

        }
    }

    public void iteratePageRank(Page p) {
        double pr = 0;

        for (Page po : this.pages()) {

            if (po.hasLinkTo(p.url())) {
                pr += po.pageRank / (double)po.getNoLinks();
            }

        }

        pr = 0.85 * pr  + 0.15;
        p.pageRank = pr;

    }
}
