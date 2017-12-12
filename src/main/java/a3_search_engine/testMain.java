package a3_search_engine;

import a2_clustering.FileSysDB;
import org.junit.*;

import java.io.File;
import java.util.ArrayList;


public class testMain {

 @Before
 public void setup()  {
     PageDB db = new PageDB();
     FileSysDB.getWordToId();
     FileSysDB.getPages();

     ArrayList<File> wordBags = FileSysDB.allBags("/defaultTestData/Words");
     ArrayList<File> linkBags = FileSysDB.allBags("/defaultTestData/Links");


     for (int i = 0; i < wordBags.size(); i++) {
         db.generatePage(FileSysDB.lastInPath(wordBags.get(i).toString()), wordBags.get(i), linkBags.get(i)); // takes the filename as url which is the wikipedia url minus the /wiki etc
     }

     db.calculatePageRank();
     FileSysDB.savePages(db.pages, db.wordToId);

     db.query("functional programming");
     db.query("java programming");
     db.query("code syntax");
     db.query("alan turing");
     db.query("dynamic allocation");
     db.query("nintendo");
     db.query("super mario");
     db.query("skyrim");



     db.query("hamburger");
     db.query("china shanghai asia");
     db.query("java");

     db.query("code");

     db.query("code syntax");
     db.query("alan turing");






 }

 @Test
    public void testStuff() {

 }

}


