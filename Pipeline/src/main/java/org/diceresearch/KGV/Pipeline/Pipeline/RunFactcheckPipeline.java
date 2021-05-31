package org.diceresearch.KGV.Pipeline.Pipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.SimpleRdfRandomIdTransform;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class RunFactcheckPipeline {
    static String predicate = "doctoralAdvisor";

    static String adr ="/home/farshad/repos/KGV/ResultsFactCheck";

    static String sss = predicate+"_FactCheck";
    static String ProgressFileName = adr+"/prf_FactCheck_"+sss+".txt";
    static String FileNameValid = adr+"/resultIsValid_FactCheck_"+sss+".txt";
    static String FileName = adr+"/result_FactCheck_"+sss+".txt";

    static HashMap<String,String> progress = new HashMap<>();



    public static void main(String[] args) throws Exception {
        Run();
    }

    public static String key(SimpleRDF input){
        return input.getSubject()+input.getPredicate()+input.getObject();
    }

    public static void Run(String _predicate,int _pathLen,boolean _isVirtual) throws Exception{
        predicate = _predicate;

        sss = predicate+"_FactCheck";
        ProgressFileName = adr+"/prf_FactCheck_"+sss+".txt";
        FileNameValid = adr+"/resultIsValid_FactCheck_"+sss+".txt";
        FileName = adr+"/result_FactCheck_"+sss+".txt";
        Run();
    }

    public static void Run() throws Exception {
        //HDDManager m = new HDDManager();

        //String _1000File = m.giveFileLocation("Converted1000FileYago3.txt");

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



        IExtractor extractor = new FileExtractor();

        File rdf4Check = (File) extractor.Extract("Co" +
                "nverted1000FileYago3.txt");

        SimpleRdfRandomIdTransform transform = new SimpleRdfRandomIdTransform();

        List<SimpleRDF> forCheck = transform.Transform(rdf4Check," ");

        HttpRequestRunner hrr = new HttpRequestRunner(new RestTemplateBuilder());

        String url = "http://localhost:8888/api/checkfact?";

        //.replace("property","ontology")
        for(SimpleRDF fact : forCheck){

            if(!fact.getPredicate().contains(predicate)){
                continue;
            }

            if(progress.containsKey(key(fact))){
                System.out.println("it was progressed before"+fact);
                continue;
            }

            System.out.println("Check Fact"+fact);

            String respons ="";

            String urlForFactCheck = url + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&predicate=" + fact.getPredicate();

            respons = hrr.sendHTTPGetRequest(urlForFactCheck);

            System.out.println(respons);
            System.out.println("-----------------------------");

            try(FileWriter fw = new FileWriter(FileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(urlForFactCheck);
                out.println(respons);
                out.println("-----------------------------");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(respons);




            try(FileWriter fw = new FileWriter(FileNameValid, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(fact.getSubject()+"\t"+fact.getPredicate()+"\t"+fact.getObject()+"\t"+json.get("defactoScore").toString());
            } catch (IOException e) {

            }

            progress.put(key(fact),"done");
            updateProgress();
            Thread.sleep(500);
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

