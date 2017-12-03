package a2_clustering;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Iterator;

public class Cluster implements Iterable<Article> {
    public Cluster left;
    public Cluster parent;
    public Cluster right; // null if article
    Article article; // null if branch
    double distance; // 0 if article
    private int size = 0;

    public Cluster(Article art) {
        size ++;
        article = art;
    }

    public Cluster() {

    }

    @JsonProperty
    public int size() { return size; }

    @JsonProperty
    public Cluster left() { return left; }

    @JsonProperty
    public Cluster parent() { return parent; }

    @JsonProperty
    public Article article() { return article; }



    public Cluster merge(Cluster oc, double distance) {

        // parent cluster node
        Cluster p = new Cluster();

        p.left = this;
        p.size += this.size();
        this.parent = p;
        p.right = oc;
        p.size += oc.size();
        oc.parent = p;

        // merge article list by calculating average of each entry

        Article nA = new Article("");
        for (int i = 0; i < article.words.size(); i++) {
            Word wA = article.words.get(i);
            Word wB = oc.article.words.get(i);
            double nCnt = (wA.count + wB.count) / 2.0;
            nA.addWord(new Word(wA.word, nCnt));
        }

        p.article = nA;

        p.distance = distance;


        return p;
    }

    @Override
    public String toString() {
        if (article != null) return article.article;
        return "";
    }

    public Iterator<Article> iterator() {
        ArrayList<Article> allArticles = new ArrayList<>();

        // just add this.right?

        // todo DRY
        Cluster c = this;
        while (c.left != null) {
            allArticles.add(c.right.article);
            c = c.left;
        }

        return allArticles.iterator();
    }

}
