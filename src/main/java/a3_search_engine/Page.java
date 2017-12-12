package a3_search_engine;

import java.util.ArrayList;

public class Page {
    private String url;
    private ArrayList<Integer> words;
    private ArrayList<String> links;
    Double pageRank = 1.0;

    public Page(String url, ArrayList<Integer> words, ArrayList<String> links) {
        this.url = url;
        this.words = words;
        this.links = links;
    }

    public ArrayList<Integer> words() { return words; }
    public ArrayList<String> links() { return links; }
    public String url() { return url; }
    public Double getPageRank() { return pageRank; }
    public void setPageRank(Double pr) { this.pageRank = pr; }

    public boolean hasLinkTo(String url) {
        return links.indexOf("/wiki/" + url) > 0;
    }

    public int getNoLinks() { return links.size(); }
}
