package a1_webCrawler;

public class WikipediaCrawler {

    public WikipediaCrawler(String[] args, int crawlDepth) {

        for (String url: args) {
            LinkNode root = new LinkNode("/wiki/" + url);
            CrawlRoot op = new CrawlRoot(root, crawlDepth);
            Output out = new Output(op.bfsResult, root);
        }
    }

}
