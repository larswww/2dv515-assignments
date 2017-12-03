package a2_clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

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
