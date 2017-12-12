package a2_API;

import a2_clustering.*;
import a3_search_engine.PageDB;
import a3_search_engine.SearchResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import a1_webCrawler.*;

import java.util.ArrayList;

@CrossOrigin
@RestController
public class Controller {
    private HtmlView html = new HtmlView();
    private PageDB search = new PageDB();

    @RequestMapping("/blogdata/centroids")
    public String blogdataCentroids() {
            return html.CentroidList(new KMeans(new ClusteringDB("/Users/mbp/Documents/Code/2dv515/a1api/seedData/blogdata.txt"), 4).getCentroids());
    }

    @RequestMapping("/blogdata/hierarchical")
    public String blogDataHtml() {
        return html.tree(new Hierarchical(new ClusteringDB("/Users/mbp/Documents/Code/2dv515/a1api/seedData/blogdata.txt")).getClusters(), "blogdata");
    }

    @RequestMapping("/wikipedia")
    public String wikipediaHtml(@RequestParam(value="algo") String algo,
                                @RequestParam(value="wiki_pages") String pages,
                                @RequestParam(value="crawl_depth") Integer crawl_depth) {
        String[] toCrawl = FileSysDB.wordBagsFor(pages);
        if (toCrawl[0] != null) new WikipediaCrawler(toCrawl, crawl_depth);
        String wikiDataPath = new ParseWikipediaData(pages).dataFileLocation();
        ClusteringDB wiki = new ClusteringDB(wikiDataPath);

        switch (algo) {

            case "kmeans":
                int noCentroids = pages.split(",").length;
                return html.CentroidList(new KMeans(wiki, noCentroids).getCentroids());

            case "hierarchical":

                String filePath = FileSysDB.matchingDataFileFor(pages, "/html");
                if (filePath != null && !filePath.equals("not found")) return FileSysDB.fileTostring(filePath); // no need to run time consuming alo again..
                return html.tree(new Hierarchical(wiki).getClusters(), pages);

            default:
                return "Invalid Request Params";
        }

    }

    @RequestMapping("/search")
    public String searchResult(@RequestParam(value="query") String query) {
        return search.query(query);
    }

}
