package a2_clustering;
import org.junit.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class testMethod {
    List<Cluster> clusters = new ArrayList<Cluster>();
    ArrayList<Article> articles;
    ArrayList<String> html = new ArrayList<>();
    ClusteringDB db;
    ArrayList<Centroid> centroids = new ArrayList<>();

    @Before
    public void setup() {
        db = new ClusteringDB("/Users/mbp/Documents/Code/2dv515/a1api/localDb/blogdata.txt");

//        new ParseWikipediaData("/Users/mbp/Documents/Code/2dv515/A1/providedData/Words/unified");

        new PrintHtml(html);
    }

    @Test
    public void testStuff() {

    }



    private void addNodes(DefaultMutableTreeNode tnode, Cluster c) {

        if (c.left != null) {
            DefaultMutableTreeNode nNode = new DefaultMutableTreeNode(c.left.toString());
            tnode.add(nNode);
            addNodes(nNode, c.left);
        }

        if (c.right != null) {
            DefaultMutableTreeNode nNode = new DefaultMutableTreeNode(c.right.toString());
            tnode.add(nNode);
            addNodes(nNode, c.right);
        }

    }
}
