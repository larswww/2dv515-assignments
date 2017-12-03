package a2_API;

import a2_clustering.Article;
import a2_clustering.Centroid;
import a2_clustering.Cluster;
import a2_clustering.FileSysDB;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

class HtmlView {
    private ArrayList<String> html;
    private String pages;
    private String htmlDataPath = "/data/html";

    String CentroidList(ArrayList<Centroid> centroids) {
        StringBuilder ct = new StringBuilder();
        int colSize;
        if (centroids.size() > 12) {
            colSize = 2;
        } else {
            colSize = 12 / centroids.size();
        }

        for (int i = 0; i < centroids.size(); i++) {
            ct.append("<div class='col-md-" + colSize + "'>");
            ct.append("<div><h3>").append("Centroid " + centroids.get(i).center().article).append("</h3><ul>");
            ct.append("</div>");


            Iterator it = centroids.get(i).cluster().iterator();
            while (it.hasNext()) {
                Article a = (Article) it.next();
                ct.append("<li>").append(a.article).append("</li>");
            }

            ct.append("</ul></div>");
        }

        return ct.toString();
    }

    public String tree(ArrayList<Cluster> clusters, String pages) {
        this.pages = pages;
        html = new ArrayList<>();
        html.add("<ul>");
        html.add("</ul>");

        addNodes(1, clusters.get(0));

        return PrintHtml(html);

    }

    public String PrintHtml(ArrayList<String> html) {
        StringBuilder sb = new StringBuilder();
        html.forEach(s -> sb.append(s).append("\n"));
        FileSysDB.PrintLinesToFile(sb.toString(), pages, htmlDataPath);
        return sb.toString();
    }

    private void addNodes(int i, Cluster c) {
        if (c.right != null) generateHtml(i, c.right);
        if (c.left != null) generateHtml(i, c.left);
    }

    private void generateHtml(int i, Cluster c) {

        String art = c.toString();
        if (art.equals("")) {
            html.add(i, "<li><ul>");
            html.add(i+1, "</ul></li>");
        } else {
            art = art.replaceAll("\"", "'");
            html.add(i, "<li data-jstree='{\"disabled\":true}'>" + art + "</li>");
        }
        addNodes(i + 1, c);
    }

}
