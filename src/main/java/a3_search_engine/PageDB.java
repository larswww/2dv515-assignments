package a3_search_engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PageDB {
    HashMap<String, Integer> wordToId;
    ArrayList<Page> pages;

    public int getIdForWord(String word) {
        if (wordToId.containsKey(word)) {
            return wordToId.get(word);
        } else {
            int id = wordToId.size();
            wordToId.put(word, id);
            return id;
        }
    }

    public ArrayList<Page> pages() { return this.pages(); }

    private void generatePage(String url, File wordsFile) {

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

            Page page = new Page(url, words);
            pages.add(page);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void query(String query) {
        ArrayList<SearchResult> result = new ArrayList<>();

        double[] content  = new double[this.pages().size()];
        double[] location = new double[this.pages().size()];

        for (int i = 0; i < this.pages().size(); i ++) {
            Page page = this.pages().get(i);
            content[i] = getCountFrequencyScore(page, query);
            content[i] = getCountLocationScore(page, query);
        }

        normalizeScores(content, false);
        normalizeScores(location, true);

        for (int i = 0; i < this.pages().size(); i++) {
            Page p = this.pages().get(i);
            double score = 1.0 * content[i] + 0.5 * location[i];
            result.add(new SearchResult(p, score));
        }

        Collections.sort(result);

    }

    public double getCountFrequencyScore(Page page, String query) {
        return 0.0;
    }

    public double getCountLocationScore(Page page, String query) {
        return 0.0;
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
}
