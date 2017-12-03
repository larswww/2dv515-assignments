package a1_webCrawler;

public class WikipediaCrawler {

    public WikipediaCrawler(String[] args) {

        for (String url: args) {
            LinkNode root = new LinkNode("/wiki/" + url);
            CrawlRoot op = new CrawlRoot(root);
            Output out = new Output(op.bfsResult, root);
        }
    }

}
