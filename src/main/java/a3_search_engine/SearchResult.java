package a3_search_engine;

public class SearchResult implements Comparable<SearchResult> {
    Page page;
    double score;

    public SearchResult(Page page, double score) {
        this.page = page;
        this.score = score;
    }

    @Override
    public int compareTo(SearchResult o) {
        if (o == null) throw new NullPointerException();

        if (o.score > this.score) return 1;
        if (o.score == this.score) return 0;

        return -1;

    }
}
