package a3_search_engine;

import java.util.ArrayList;

public class Page {
    String url;
    ArrayList<Integer> words;

    public Page(String url, ArrayList<Integer> words) {
        this.url = url;
        this.words = words;
    }
}
