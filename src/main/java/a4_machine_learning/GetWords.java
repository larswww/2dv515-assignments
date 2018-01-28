package a4_machine_learning;

import java.util.HashMap;

public class GetWords {

    public GetWords() {

    }

    public HashMap<String, Integer> getFeatures(String doc) {
        HashMap<String, Integer> uniqueDict = new HashMap<>();

        String[] words = doc.split("\\W");
        // split the words by non alpha characters
        for (String w : words) {
            if (w.length() > 2 && w.length() < 20) {
                uniqueDict.putIfAbsent(w, 0);
                uniqueDict.put(w, uniqueDict.get(w) + 1);
            }
        }

        return uniqueDict;
    }

}

