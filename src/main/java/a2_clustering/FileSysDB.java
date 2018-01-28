package a2_clustering;

import a3_search_engine.Page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileSysDB {
    private static String dataDir = System.getProperty("user.dir") + "/data";

    public static String PrintLinesToFile(String lines, String fileNameStart, String dbDir) {
        String timeStamp = new SimpleDateFormat("dd.HH.mm.ss").format(new Date());

        File file = new File( dataDir + dbDir);
        file = new File(file, fileNameStart + " " + timeStamp + ".txt");

        try {
            if (!file.exists()) file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            fw.write(lines);
            fw.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return Paths.get(file.toString()).toString();
    }

    public static String[] wordBagsFor(String pages) {
        String[] pagesArr = pages.split(",");
        String[] toCrawl = new String[pagesArr.length];
        toCrawl[0] = null;
        int count = 0;

        for (String page : pagesArr) {
            if (!new File(dataDir + "/crawler/Words/" + page).exists()) toCrawl[count++] = page;
        }

        return toCrawl;
    }

    public static String matchingDataFileFor(String pages, String parsedDataPath) {
        try {
            Iterator it = Files.list(Paths.get(dataDir + parsedDataPath)).iterator();
            while (it.hasNext()) {
                Path p = (Path) it.next();
                if (FileSysDB.lastInPath(p.toString()).startsWith(pages)) {
                    return p.toString();
                }
            }

        } catch (Exception e) { return "not found"; }
        return "not found";
    }

    public static ArrayList<File> allBags(String path) {
        ArrayList<File> wordbags = new ArrayList<File>();

        try {
            Iterator it = Files.list(Paths.get(dataDir + path)).iterator();

            while (it.hasNext()) {
                Path p = (Path) it.next();
                Iterator files = Files.list(p).iterator();
                while (files.hasNext()) wordbags.add(new File(files.next().toString()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordbags;

    }

    public static void savePages(ArrayList<Page> pages, HashMap<String, Integer> wordToId) {
        StringBuilder wti = new StringBuilder();

        wordToId.forEach((word, wId) -> {
            wti.append(word).append("\t").append(wId.toString()).append("\n");
        });

        PrintLinesToFile(wti.toString(), "wordToId", "/pageDB");

        StringBuilder pageString = new StringBuilder();

        for (Page p : pages) {
            pageString.append(p.url()).append("\n");


            for (Integer i : p.words()) {
                pageString.append(i).append("\t");
            }
            pageString.append("\n");

            for (String link : p.links()) {
                pageString.append(link).append("\t");
            }
            pageString.append("\n");
            pageString.append(p.getPageRank()).append("\n");
        }

        PrintLinesToFile(pageString.toString(), "pages", "/pageDB");
    }

    public static ArrayList<Page> getPages() {
        String pages = fileTostring("/Users/mbp/Documents/Code/2dv515/a1api/data/pageDB/pages 11.14.18.53.txt");
        String[] splitted = pages.split("\n");
        ArrayList<Page> pageArr = new ArrayList<>();

        for (int i = 0; i < splitted.length;) {
            String url = splitted[i++];

            String[] wordStrs = splitted[i++].split("\t"); // all the words ints separated by tabs.
            Integer[] wordInts = new Integer[wordStrs.length];
            for (int y = 0; y < wordStrs.length; y++) {
                wordInts[y] = Integer.parseInt(wordStrs[y]);
            }

            String[] links = splitted[i++].split("\t");

            double pr = Double.parseDouble(splitted[i++]);
            ArrayList<Integer> il = new ArrayList<>();
            il.addAll(Arrays.asList(wordInts));
            ArrayList<String> ls = new ArrayList<>();
            ls.addAll(Arrays.asList(links));

            Page p = new Page(url, il, ls);
            p.setPageRank(pr);
            pageArr.add(p);

        }

        return pageArr;
    }

    public static HashMap<String, Integer> getWordToId() {
        HashMap<String, Integer> wordToId = new HashMap<String, Integer>();
        String wholeThing = fileTostring("/Users/mbp/Documents/Code/2dv515/a1api/data/pageDB/wordToId 11.14.17.46.txt");
        String[] splitted = wholeThing.split("\n");

        for (String line : splitted) {
            String[] wordAndId = line.split("\t");
            wordToId.put(wordAndId[0], Integer.parseInt(wordAndId[1]));
        }


        return wordToId;
    }

    public static String fileTostring(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            return "not found";
        }
    }

    public static String lastInPath(String path) {
        String[] wholePath = path.split("/");
        return wholePath[wholePath.length -1];
    }
}
