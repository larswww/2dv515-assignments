package a2_clustering;

import java.util.ArrayList;

public class KMeans {
    ArrayList<Centroid> centroids;
    ArrayList<Article> articles;
    CalculatePearson metric = new CalculatePearson();

    public KMeans(ClusteringDB db, int noCentroids) {
        this.articles = db.articles;
        kMeans(noCentroids);
    }

    public ArrayList<Centroid> getCentroids() {
        return centroids;
    }

    private void kMeans(int noCentroids) {
        centroids = new ArrayList<>();
        Randomizer rnd = new Randomizer(articles);
        int k = noCentroids;

        for (int i = 0; i < k; i++) {
            centroids.add(rnd.createRandom());
        }

        boolean done = false;
        int cnt = 0;

        while (!done) {
            iterateCentroids();

            for (Centroid c : centroids) {
                c.recalcCenter();
            }

            done = true;

            for (Centroid c : centroids) {

                if (!c.matchesPreviousAssignment()) {
                    done = false;
                }

            }

            cnt++;
        }

        System.out.print("Iterations: " + cnt + " Centroids: " + centroids.size());

    }

    public void iterateCentroids() {

        for (Centroid c : centroids) {
            c.cluster = null;
        }

        for (int i = 0; i < articles.size(); i++) {
            double closest = Double.MAX_VALUE;
            Centroid bestCentroid = null;
            Article bestArticle = null;

            for (int j = 0; j < centroids.size(); j++) {
                Centroid centroid = centroids.get(j);
                Article a = articles.get(i);


                double distance = metric.getSimilarity(centroid.center, a);
                if (distance < closest) {
                    closest = distance;
                    bestCentroid = centroid;
                    bestArticle = a;
                }

            }

            if (bestCentroid.cluster == null) bestCentroid.cluster = new Cluster(bestCentroid.center);
            bestCentroid.cluster = bestCentroid.cluster.merge(new Cluster(bestArticle), closest);
        }
    }
}
