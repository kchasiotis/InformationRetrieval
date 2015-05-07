import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Konstantinos Chasiotis
 */
public class Query {

    int id;
    String Description;
    ArrayList<QueryWord> allWords;
    double maxFrequency;
    int CollectionSize = 84678;
    int Database;

    //Split every word of the query so you can set attributes
    private void InitializeAllWords() {
        String[] temp = Description.split(" ");

        allWords = new ArrayList();

        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].trim();
            if (!temp[i].equals("")) {
                allWords.add(new QueryWord(temp[i]));
            }
        }

    }

    public void RunQuery() {
        FindFrequency();
        FindMaxFrequency();
        FindTF();

        //TODO: Crawler finds df, exists, Collection IDs
        for (int i = 0; i < allWords.size(); i++) {
            allWords.get(i).LoadServerData(Crawler(allWords.get(i).theWord));
        }

        FindIDF();
    }

    private QueryWord Crawler(String Word) {

        try {
            QueryWord temp = new QueryWord(Word);

            String url = "http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?g=p&d=" + Database + "&v=" + Word;
            //Sample link: http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?d=0&v=verbose
            Document doc = Jsoup.connect(url).get();

            Elements webpageBody = doc.getElementsByTag("body");
            doc.select("hr").remove();
            doc.select("center").remove();

            String[] data = doc.getElementsByTag("body")
                    .text().split(" ");
            //System.out.println("The word: '" + Word + "'");
            //System.out.println(doc.getElementsByTag("body").text());

            //Get ctf
            temp.ctf = Integer.parseInt(data[0]);
            //Get df
            temp.df = Integer.parseInt(data[1]);
            for (int i = 2; i < data.length; i += 3) {
                //System.out.println(Integer.parseInt(data[i]));
                int id = Integer.parseInt(data[i]);
                int doclenght = Integer.parseInt(data[i + 1]);
                int tf = Integer.parseInt(data[i + 2]);
                
                temp.CollectionIds.add(new CollectionDoc(id, doclenght, tf));
            }
            return temp;
        } catch (IOException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void FindIDF() {
        /*Old part
         for (int i = 0; i < allWords.size(); i++) {
         allWords.get(i).idf = Math.log((double) CollectionSize / allWords.get(i).df);
         }*/
    }

    private void FindTF() {
        /* Old part
         for (int i = 0; i < allWords.size(); i++) {
         allWords.get(i).tf = (double) allWords.get(i).wordFrequency / maxFrequency;
         }*/

    }

    private void FindFrequency() {

        for (int i = 0; i < allWords.size(); i++) {
            int counter = 0;
            //Compare with all the words of the query
            for (int j = 0; j < allWords.size(); j++) {
                if (allWords.get(i).Compare(allWords.get(j)) == true) {
                    counter++;
                    allWords.get(i).wordFrequency = counter;
                    //Remove word if it is encountered more than one time
                    if (counter > 1) {
                        allWords.remove(j);
                    }
                }
            }
        }
    }

    //Finds the term with the biggest frequency
    private void FindMaxFrequency() {
        double max = 0;
        for (int i = 0; i < allWords.size(); i++) {
            if (allWords.get(i).wordFrequency > max) {
                max = allWords.get(i).wordFrequency;
            }
        }
        maxFrequency = max;
    }

    public Query(String Description, int id) {
        this.Description = SanitizeText(Description);
        this.id = id;
        InitializeAllWords();
    }

    @Override
    public String toString() {
        return "Query{" + "id=" + id + ", Description=" + Description + '}' + "\n";
    }

    private static String SanitizeText(String Text) {
        String temp = Text.replaceAll("\\p{Punct}+", " ");

        temp = temp.toLowerCase();
        return temp;
    }
}
