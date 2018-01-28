package a4_machine_learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class OwnClassifier {
    private HashMap<String, int[]> fc; // {'python': {'bad': 0, 'good': 6}, 'the': {'bad': 3, 'good': 3}} . so bad count in 0 and good count in 1.
    private HashMap<String, Integer> cc;
    private HashMap<String, Double> thresholds;
    private GetWords gw;

    // counts of feature/category combos
    // counts of documents in each cat

    public OwnClassifier(GetWords gw, ArrayList<String> cats) {
        fc = new HashMap<>();
        cc = new HashMap<>();
        for (String cat : cats) cc.put(cat, 0);
        this.gw = gw;
        thresholds = new HashMap<>();

    }

    // increase the count of feature/category pair
    public void incf(String f, String cat) {
        int c = catToInt(cat);
        fc.putIfAbsent(f, new int[2]);
        int[] counts = fc.get(f);
        counts[c] += 1;
        fc.put(f, counts);

    }

    // increase the count of a category
    public void incc(String cat) {
        cc.putIfAbsent(cat, 0);
        int count = cc.get(cat);
        cc.put(cat, count += 1);
    }


    // no of times a feature has appeared in a category
    public int fcount(String f, String cat) {
        int c = catToInt(cat);

        if (fc.containsKey(f)) {
            return fc.get(f)[c]; //todo do i need to use doubles instead of ints?
        } else {
            return 0;
        }
    }

    // number of items in category
    public int catcount(String cat) {
        return cc.get(cat);
    }

    // total number of items
    public int totalcount() {
        Integer res = 0;
        Iterator it = cc.keySet().iterator();

        while (it.hasNext()) res += cc.get(it.next());

        return res;
    }


    // list of all categories
    public Set<String> categories() {
        return cc.keySet();
    }

    public void train(String item, String cat) {
        HashMap<String, Integer> features = gw.getFeatures(item);

        features.forEach((f, v) -> {
            incf(f, cat);
        });

        incc(cat);
    }

    public double fprob(String f, String cat) {
        if (catcount(cat) == 0 ) return 0.0;
        // total number of times this feature appeared in this category
        // divided by the total number of items in this category
        // Pr (A | B) probability of A given B == Pr (Word | Classification);

        return Double.parseDouble(Integer.toString(fcount(f, cat))) / Double.parseDouble(Integer.toString(catcount(cat)));
    }


    public double weightedprob(String f, String cat, double weight, double ap) {
        double basicprob = fprob(f, cat);

        // total times of f in all categories
        double totals = 0;
        for (String c : categories()) {
            totals += fcount(f, c);
        }

        double bp = ((weight * ap) + (totals * basicprob)) / (weight + totals);
        return bp;
    }

    private int catToInt(String cat) {
        if (!cc.containsKey(cat)) cc.put(cat, 0);
        int count = 0; // dynamically verify category to support varied category names and more than two.
        for (String csd : cc.keySet()) {
            if (csd.equals(cat)) return count;
            count++;
        }

        return -111111; // this value should never be returned
    }


    public double docprob(String item, String cat) {
        HashMap<String, Integer> features = gw.getFeatures(item);

        // multiply the probabilities of all the features together
        double p = 1.0;
        double previousp = 1.0;

        for (String f : features.keySet()) {
            previousp = p;
            if (p == 1.0E-323) {
            }

            p *= weightedprob(f, cat, 1.0, 0.5);

        }

        return p;
    }

    public double prob(String item, String cat) {
        double catprob = Double.parseDouble(Integer.toString(catcount(cat))) / Double.parseDouble(Integer.toString(totalcount()));
        double docprob = docprob(item, cat);
        return docprob * catprob;
    }

    public void setthresholds(String cat, Double t) {
        thresholds.put(cat, t);
    }

    public double getthreshold(String cat) {
        if (!thresholds.containsKey(cat)) return 1.0;
        return thresholds.get(cat);
    }

    public String classify(String item) {
        HashMap<String, Double> probs = new HashMap<>();
        double max = 0.0;
        String best = "";
        //find the category with the highest probability


        for (String cat : categories()) {
            probs.put(cat, prob(item, cat));
            if (probs.get(cat) > max) {
                max = probs.get(cat);
                best = cat;
            }
        }

        // make sure the probability exceeds threshold*next best

        for (String cat : probs.keySet()) {
            if (cat.equals(best)) continue;
            if (probs.get(cat) * getthreshold(best) > probs.get(best)) return "unknown";
        }

        return best;

    }
}
