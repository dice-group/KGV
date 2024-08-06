package org.diceresearch.KGV.Pipeline.Pipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.*;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

//run coppal pipeline , read file for each fact call copaal and save result
public class RunCopaalPipeline {
    String fileNameConst ;
    String copaalEndPoint;

    String locationForSaveTheResults ;
    String fileToProcess ;

    String ProgressFileName;
    String FileNameValid;
    String FileName;

    boolean reverseTheSubjectAndObject = false;

    Set<String> acceptAblePredicates;

    static HashMap<String,String> progress = new HashMap<>();

    public static void main(String[] args) throws Exception {
        //String fileToProcess = "/home/farshad/repos/KGV/KGEvalData/YAGO/yago_beliefs.ttl";
        String fileToProcess = "/home/farshad/experiments/yago2Copaal/subsetResultRegenarated/check_copaal_nowriter.txt";
        String locationForSaveTheResults = "/home/farshad/experiments/yago2Copaal/subsetResultRegenarated/results/";
        String fileNameConst = "yagoSubset2remainingstriples";
        String copaalEndPoint = "http://synthg-fact-leapfrog.cs.upb.de:8484/api/v1/validate?";
        //String copaalEndPoint = "http://localhost:8082/api/v1/validate?";
        String acceptablePredicates = "http://dbpedia.org/ontology/birthPlace";
        RunCopaalPipeline p = new RunCopaalPipeline(fileToProcess, locationForSaveTheResults, fileNameConst, copaalEndPoint);//,acceptablePredicates);
        p.run();
    }

    public RunCopaalPipeline(String fileToProcess , String locationForSaveTheResults, String fileNameConst, String copaalEndPoint){
        this.locationForSaveTheResults = locationForSaveTheResults;
        this.fileToProcess = fileToProcess;
        this.fileNameConst = fileNameConst;
        this.copaalEndPoint = copaalEndPoint;
    }

    public RunCopaalPipeline(String fileToProcess , String locationForSaveTheResults, String fileNameConst, String copaalEndPoint, String acceptablePredicates){
        this.locationForSaveTheResults = locationForSaveTheResults;
        this.fileToProcess = fileToProcess;
        this.fileNameConst = fileNameConst;
        this.copaalEndPoint = copaalEndPoint;
        String[] predicates = acceptablePredicates.split(",");
        System.out.println(predicates.length+ " predicates are acceptable");

        this.acceptAblePredicates = new HashSet<String>();
        for(String p:predicates){
            this.acceptAblePredicates.add(p);
        }
    }


    private String key(SimpleRDF input){
        return input.getSubject()+input.getPredicate()+input.getObject();
    }

    public void run() throws Exception {
        System.out.println("Start running the pipeline");
        ProgressFileName = locationForSaveTheResults+"2prf_"+fileNameConst+".txt";
        FileNameValid = locationForSaveTheResults+"0shortResults_"+fileNameConst+".txt";
        FileName = locationForSaveTheResults+"1resultWithDetail_"+fileNameConst+".txt";


        System.out.println("ProgressFileName is : "+ProgressFileName);
        System.out.println("FileNameValid is : "+FileNameValid);
        System.out.println("FileName is : "+FileName);

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



        IExtractor extractor = new FileExtractorOutOfPackage();

        File rdf4Check = (File) extractor.Extract(fileToProcess);

        //SimpleRdfRandomIdTransform transform = new SimpleRdfRandomIdTransform();

        //TTLtoSimpleRDFTransform transform = new TTLtoSimpleRDFTransform();
        //RDFModelTransform rdfTransform = new RDFModelTransform();

        //List<SimpleRDF> forCheck = transform.transform(rdfTransform.transform(rdf4Check,"")," ");
        CopaalOutPutTransform transform = new CopaalOutPutTransform();
        List<SimpleRDF> forCheck = transform.transform(rdf4Check,"\t");

        System.out.println(forCheck.size() + " facts for check extracted");

        HttpRequestRunner hrr = new HttpRequestRunner(new RestTemplateBuilder());

        //.replace("property","ontology")
        int counter = 0;
        for(SimpleRDF fact : forCheck){
            if(acceptAblePredicates!=null){
                if(!acceptAblePredicates.contains(fact.getPredicate())){
                    continue;
                }
            }
            counter = counter +1;

/*            if(!fact.getPredicate().contains(predicate)){
                continue;
            }*/

            if(progress.containsKey(key(fact))){
                System.out.println("it was progressed before"+fact);
                continue;
            }

            System.out.println("Check Fact"+fact);

            String respons ="";
            String urlForCoppal = "";

            if(reverseTheSubjectAndObject){
                urlForCoppal = copaalEndPoint + "subject=" + fact.getObject() + "&object=" + fact.getSubject() + "&property=" + fact.getPredicate();
            }else {
                urlForCoppal = copaalEndPoint + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&property=" + fact.getPredicate();
            }

            respons = hrr.sendHTTPGetRequest(urlForCoppal);

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
                out.println(fact.getSubject()+"\t"+fact.getPredicate()+"\t"+fact.getObject()+"\t"+json.get("veracityValue").toString()+"\t"+fact.getId());
            } catch (IOException e) {

            }

            progress.put(key(fact),"done"+counter);
            updateProgress();
            Thread.sleep(2000);
        }

    }

    private void updateProgress() {
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
