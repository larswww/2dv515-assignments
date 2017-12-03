package a2_clustering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClusteringDB {
    ArrayList<Article> articles = new ArrayList<>();
    String[] words;
    ArrayList<Cluster> clusters = new ArrayList<>();
    String strPath;


    // take all the words, first line
    // take each blog line
    // iterate that and words simultaneously


    public ClusteringDB(String strPath) {
        try {
            Path path = Paths.get(strPath);
            BufferedReader br = new FileOpener(path).br;
            firstLine(br);
            blogLines(br);
            br.close();
            generateClusters();
            this.strPath = strPath;
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't find file check path");

        } catch (IOException e) {
            System.err.println(e.getMessage());

        }

    }

    private void firstLine(BufferedReader br) throws IOException {
        String first = br.readLine();
        words = first.split("\t");
    }

    private void blogLines(BufferedReader br) throws IOException {

        String line = br.readLine();
        while (line != null) {
            String[] splittedBlogLine = line.split("\t");
            Article a = blogLineToArticle(splittedBlogLine);
            articles.add(a);
            line = br.readLine();
        }
    }

    private Article blogLineToArticle(String[] splittedBlogLine) {
        String blogName = splittedBlogLine[0];
        Article a = new Article(blogName);

        for (int i = 1; i < words.length; i++) {
            Word w = new Word(words[i], Double.parseDouble(splittedBlogLine[i]));
            a.addWord(w);
        }

        return a;
    }

    private void generateClusters() {
        for (Article art : articles )
        {
            clusters.add(new Cluster(art));
        }
    }


}
