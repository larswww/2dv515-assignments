package a2_clustering;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Article {
    @JsonProperty
    public String article;
    public List<Word> words = new ArrayList<Word>();

    public Article(String a) {
        article = a;
    }

    public void addWord(Word w) {
        words.add(w);

    }
}
