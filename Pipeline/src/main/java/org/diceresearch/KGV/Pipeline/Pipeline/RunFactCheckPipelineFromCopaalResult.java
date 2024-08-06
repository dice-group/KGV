package org.diceresearch.KGV.Pipeline.Pipeline;


import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.CopaalOutPutTransform;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.List;

// as input receive a copaal output and ruin fact check for all 0.0 veracities
public class RunFactCheckPipelineFromCopaalResult {

    private static String fileToProcess ="/home/farshad/experiments/yago2Copaal/subsetResultRegenarated/check_factcheck.txt";

    static String locationForSaveTheResults ="/home/farshad/experiments/yago2Copaal/subsetResultRegenarated/results4notexisttriplesinbelievesresults/";

    static String sss= "remainingtriples";
    static String ProgressFileName = locationForSaveTheResults+"/5prf_FactCheck_"+sss+".txt";
    static String FileNameValid = locationForSaveTheResults+"/3resultIsValid_FactCheck_"+sss+".txt";
    static String FileName = locationForSaveTheResults+"/4result_FactCheck_"+sss+".txt";
    static HashMap<String,String> progress = new HashMap<>();
    public static void main(String[] args) throws Exception {
        Run();
    }
    public static String key(SimpleRDF input){
        return input.getSubject()+input.getPredicate()+input.getObject();
    }

    private static void Run() throws Exception {
        IExtractor extractor = new FileExtractorOutOfPackage();

        File outPutOfCopaal = (File) extractor.Extract(fileToProcess);
        CopaalOutPutTransform transformer = new CopaalOutPutTransform();
        List<SimpleRDF> forCheck = transformer.transform(outPutOfCopaal, "\t");

        HttpRequestRunner hrr = new HttpRequestRunner(new RestTemplateBuilder());

        String url = "http://localhost:8080/api/checkfact?";

        File prf = new File(ProgressFileName);
        if(prf.exists()){
            try {
                FileInputStream fileIn = new FileInputStream(ProgressFileName);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                progress = (HashMap) in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            } catch (ClassNotFoundException c) {
                System.out.println("Employee class not found");
                c.printStackTrace();
            }
        }


        int i = 0 ;
        //.replace("property","ontology")
        for (SimpleRDF fact : forCheck) {

            if (fact.getCorrectness()) {
                continue;
            }
// i = i+1;
//            if(i<590){
//                continue;
//            }

            System.out.println("Check Fact" + fact);

            String respons = "";

            String urlForFactCheck = url + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&predicate=" + fact.getPredicate().replace("property","ontology");

            respons = hrr.sendHTTPGetRequest(urlForFactCheck);

            System.out.println(respons);
            System.out.println("-----------------------------");

            try (FileWriter fw = new FileWriter(FileName, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(urlForFactCheck);
                out.println(respons);
                out.println("-----------------------------");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(respons);


            try (FileWriter fw = new FileWriter(FileNameValid, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(fact.getSubject() + "\t" + fact.getPredicate() + "\t" + fact.getObject() + "\t" + json.get("defactoScore").toString()+"\t"+fact.getId());
            } catch (IOException e) {

            }
            progress.put(key(fact),"done");
            updateProgress();
        }
    }

    private static void updateProgress() {
        FileOutputStream fileOut = null;
        try {
            fileOut =
                    new FileOutputStream(ProgressFileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            System.out.println("updatePr");
            out.writeObject(progress);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in"+ ProgressFileName+ "progress size is :"+progress.size());
        } catch (IOException i) {
            i.printStackTrace();
        }finally {
            System.out.println("Any body here ...");
            try {
                if (fileOut != null) {
                    System.out.println("CloseConn");
                    fileOut.close();
                    System.out.println("CloseConnDone");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }

}
