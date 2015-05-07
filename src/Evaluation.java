import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Evaluation {

    double recall;
    double precision;
    double standard11;
    double r_precision;
    int QueryId;

    //All retrieved docs
    int retrievedDocsTotal;
    //Number of relevant docs retrieved
    int relevantDocsRetrieved;

    //Number of relevant documents retrieved
    int relevantRetrievedDocs;
    //Number of all relevant documents
    int relevantDocsTotal;

    ArrayList<Integer> retrievedList;
    ArrayList<Integer> evaluationList;

    public void CalcRecall() {
        recall = (double) relevantRetrievedDocs / relevantDocsTotal;
    }

    public void CalcPrecision() {
        precision = (double) relevantDocsRetrieved / retrievedDocsTotal;
    }

    public void CalcRPrecision() {
        int counter = 0;
        for (int i = 0; i < retrievedList.size(); i++) {
            if (evaluationList.contains(retrievedList.get(i))) {
                counter++;
            }
        }
        r_precision = counter / relevantDocsTotal;
    }

    public ArrayList LoadEvaluationList(String filepath) {

        ArrayList<Query> list = new ArrayList();
        try {

            BufferedReader in = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = in.readLine()) != null) {
                //Save query id and query description
                int QueryId = Integer.parseInt(line.split(" ")[0]);
                int DocId = Integer.parseInt(line.split(" ")[1]);

                if (this.QueryId == QueryId) {
                    evaluationList.add(DocId);
                }
            }
            in.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public Evaluation(ArrayList<Integer> retrievedList) {
        this.retrievedList = retrievedList;
        //todo
    }

    public void RunEvaluation() {
        CalcPrecision();
        CalcRPrecision();
        CalcRecall();
        //TODO 11points
    }

}
