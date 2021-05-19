package org.diceresearch.KGV.Pipeline.Pipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.jena.dboe.sys.Sys;
import org.diceresearch.KGV.ETL.DiskManager.HDDManager;
import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Load.FileLoader;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.SimpleRdfRandomIdTransform;
import org.diceresearch.KGV.ETL.Transform.ToDBpediaTransform;
import org.diceresearch.KGV.ETL.Transform.ToHashMapTransform;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class RunCopaalPipeline {
    static int pathLen = 2;
    static String predicate = "location";
    static boolean isVirtual = false;

    static String adr ="/home/farshad/repos/KGV/Results";

    static String sss = predicate+"_VT_"+isVirtual+"_pl_"+pathLen;
    static String ProgressFileName = adr+"/prf_"+sss+".txt";
    static String FileNameValid = adr+"/resultIsValid_"+sss+".txt";
    static String FileName = adr+"/result_"+sss+".txt";

    static HashMap<String,String> progress = new HashMap<>();



    public static void main(String[] args) throws Exception {
        Run();
    }

    public static String key(SimpleRDF input){
        return input.getSubject()+input.getPredicate()+input.getObject();
    }

    public static void Run(String _predicate,int _pathLen,boolean _isVirtual) throws Exception{
        pathLen = _pathLen;
        predicate = _predicate;
        isVirtual = _isVirtual;

        sss = predicate+"_VT_"+isVirtual+"_pl_"+pathLen;
        ProgressFileName = "/home/farshad/repos/KGV/prf_"+sss+".txt";
        FileNameValid = "/home/farshad/repos/KGV/resultIsValid_"+sss+".txt";
        FileName = "/home/farshad/repos/KGV/result_"+sss+".txt";
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

        String url = "http://localhost:8282/api/v1/validate?";

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
            String urlForCoppal = "";



            if(isVirtual) {
                urlForCoppal = url + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&property=" + fact.getPredicate() + "&virtualType=True+&pathlength="+pathLen;
            }else {
                urlForCoppal = url + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&property=" + fact.getPredicate() + "&virtualType=False+&pathlength="+pathLen;
            }
            respons = hrr.sendHTTPGetRequest(urlForCoppal);
            /*if(fact.getPredicate().contains("ontology")) {
                urlForCoppal = url + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&property=" + fact.getPredicate() + "&virtualType=True";
                 respons = hrr.sendHTTPGetRequest(urlForCoppal);
            }else {
                urlForCoppal = url + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&property=" + fact.getPredicate() + "&virtualType=True";
                respons = hrr.sendHTTPGetRequest(urlForCoppal);
            }*/
            System.out.println(respons);
            System.out.println("-----------------------------");

            try(FileWriter fw = new FileWriter(FileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(urlForCoppal);
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
                out.println(fact.getSubject()+"\t"+fact.getPredicate()+"\t"+fact.getObject()+"\t"+json.get("graphScore").toString());
            } catch (IOException e) {

            }

            progress.put(key(fact),"done");
            updateProgress();
            Thread.sleep(2000);
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
