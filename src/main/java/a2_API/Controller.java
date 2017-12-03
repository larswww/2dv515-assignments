package a2_API;

import a2_clustering.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import a1_webCrawler.*;

import java.util.ArrayList;

@CrossOrigin
@RestController
public class Controller {
    private String htmlFilesPath = System.getProperty("user.dir") + "/data/html";
    private HtmlView html = new HtmlView();

    @RequestMapping("/blogdata/centroids")
    public String blogdataCentroids() {
            return html.CentroidList(new KMeans(new ClusteringDB("/Users/mbp/Documents/Code/2dv515/a1api/localDb/blogdata.txt")).getCentroids());

    }

    @RequestMapping("/blogdata/hierarchical")
    public String blogDataHtml() {
        return html.tree(new Hierarchical(new ClusteringDB("/Users/mbp/Documents/Code/2dv515/a1api/localDb/blogdata.txt")).getClusters(), "blogdata");
    }

    @RequestMapping("/wikipedia")
    public String wikipediaHtml(@RequestParam(value="algo", defaultValue ="kmeans") String algo,
                                @RequestParam(value="pages", defaultValue = "Programming,Games") String pages) {
        String[] toCrawl = FileSysDB.wordBagsFor(pages);
        if (toCrawl[0] != null) new WikipediaCrawler(toCrawl);
        String wikiDataPath = new ParseWikipediaData(pages).dataFileLocation();
        ClusteringDB wiki = new ClusteringDB(wikiDataPath);

        switch (algo) {

            case "kmeans":

                return html.CentroidList(new KMeans(wiki).getCentroids());

            case "hierarchical":

                String filePath = FileSysDB.matchingDataFileFor(pages, htmlFilesPath);
                if (filePath != null && !filePath.equals("not found")) return FileSysDB.fileTostring(filePath); // no need to run time consuming alo again..
                return html.tree(new Hierarchical(wiki).getClusters(), pages);

            default:
                return "Invalid Request Params";
        }

    }

}
