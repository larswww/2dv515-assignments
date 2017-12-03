package a2_clustering;

import java.util.ArrayList;

public class Hierarchical {
    ClusteringDB db;
    ArrayList<Article> articles;
    ArrayList<Cluster> clusters;
    CalculatePearson metric = new CalculatePearson();


    public Hierarchical(ClusteringDB db) {
        this.db = db;
        articles = db.articles;
        clusters = db.clusters;

        while (clusters.size() > 1) {
            iterate();
        }

    }

    public ArrayList<Article> getArticles() { return articles; }

    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    public void iterate() {

        double closest = Double.MAX_VALUE; //todo Double.MAX_VALUE; was leaving closest at 0.0?
        Cluster bestA = null;
        Cluster bestB = null;

        for (int i = 0; i < clusters.size(); i++) {

            for (int j = i + 1; j < clusters.size(); j++) {
                Cluster cA = clusters.get(i);
                Cluster cB = clusters.get(j);

                double distance = metric.getSimilarity(cA.article, cB.article);
                if (distance < closest) {
                    closest = distance;
                    bestA = cA;
                    bestB = cB;
                }
            }
        }

        Cluster m = bestA.merge(bestB, closest);
        clusters.add(m);

        clusters.remove(bestA);
        clusters.remove(bestB);
    }
}
