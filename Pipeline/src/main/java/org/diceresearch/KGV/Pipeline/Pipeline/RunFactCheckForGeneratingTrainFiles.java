package org.diceresearch.KGV.Pipeline.Pipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.diceresearch.KGV.ETL.Transform.TTLtoSimpleRDFTransform;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.List;

    // as input receive a copaal output and run fact check for all 0.0 veracities
    public class RunFactCheckForGeneratingTrainFiles {

        //private static String fileToProcess ="/home/farshad/experiments/yago2Copaal/Results/Beliefs/0shortResults_yagoBeliefs2hasChild.txt";
        private static String fileToProcess ="/home/farshad/repos/KGV/KGEvalData/YAGO/yago_beliefs.ttl";
        //private static String fileToProcess ="/home/farshad/experiments/Trainfactchek/falteFactFromQuery.txt";

        static String locationForSaveTheResults ="/home/farshad/experiments/Trainfactchek/factcheckResults/";

        static String predicate ="ALLPositive";

        static String sss= "factCheckTrainingTrue_yagoBeliefs2"+predicate;
        static String ProgressFileName = locationForSaveTheResults+"/prf_FactCheck_"+sss+".txt";
        static String ProgressFileHashMap = locationForSaveTheResults+"/prfhs_FactCheck_"+sss+".txt";
        static String FileNameValid = locationForSaveTheResults+"/3resultIsValid_FactCheck_"+sss+".txt";
        static String FileName = locationForSaveTheResults+"/4result_FactCheck_"+sss+".txt";
        static Integer threshold = 30;

        static HashMap<String,String> progress = new HashMap<>();
        static HashMap<String,Integer> numberOfGeneratedFacts = new HashMap<>();

        public static void main(String[] args) throws Exception {
            Run();
        }

        public static String key(SimpleRDF input){
            return input.getSubject()+input.getPredicate()+input.getObject();
        }

        private static void Run() throws Exception {


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
                    c.printStackTrace();
                }
            }

            File prfhm = new File(ProgressFileHashMap);
            if(prfhm.exists()){
                try {
                    FileInputStream fileIn = new FileInputStream(ProgressFileHashMap);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    numberOfGeneratedFacts = (HashMap) in.readObject();
                    in.close();
                    fileIn.close();
                } catch (IOException i) {
                    i.printStackTrace();
                } catch (ClassNotFoundException c) {
                    c.printStackTrace();
                }
            }


            IExtractor extractor = new FileExtractorOutOfPackage();

            //File outPutOfCopaal = (File) extractor.Extract(fileToProcess);
            //CopaalOutPutTransform transformer = new CopaalOutPutTransform(0.5);

            /*File trainingFile = (File) extractor.Extract(fileToProcess);
            SimpleRdfRandomIdTransform transformer = new SimpleRdfRandomIdTransform();
            List<SimpleRDF> forCheck = transformer.Transform(trainingFile, " ");*/

            File allTriples = (File) extractor.Extract(fileToProcess);
            TTLtoSimpleRDFTransform transform = new TTLtoSimpleRDFTransform();
            RDFModelTransform rdfTransform = new RDFModelTransform();
            List<SimpleRDF> forCheck = transform.transform(rdfTransform.transform(allTriples,"")," ");

            HttpRequestRunner hrr = new HttpRequestRunner(new RestTemplateBuilder());

            String url = "http://localhost:8080/api/checkfact?";

            int i = 0 ;
            //.replace("property","ontology")
            for (SimpleRDF fact : forCheck) {
                System.out.println(i);
                i = i+1;

                if(i%20==0){

                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
                    numberOfGeneratedFacts.entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });
                    System.out.println("vvvvvvvvvvvvvvvvvvvvvv");
                }
                /*if(!fact.getPredicate().contains(predicate)){
                    continue;
                }*/

//                if (!fact.getCorrectness()) {
//                    continue;
//                }
// i = i+1;
//            if(i<590){
//                continue;
//            }

                if(progress.containsKey(key(fact))){
                    System.out.println("it was progressed before"+fact);
                    continue;
                }

                if(numberOfGeneratedFacts.containsKey(fact.getPredicate())){
                    if(numberOfGeneratedFacts.get(fact.getPredicate())> threshold){
                        continue;
                    }
                }

                if(fact.getPredicate().contains("workIns")){
                    continue;
                }

                System.out.println("Check Fact" + fact);

                String respons = "";

                String urlForFactCheck = url + "subject=" + fact.getSubject() + "&object=" + fact.getObject() + "&predicate=" + fact.getPredicate();

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
                    out.println(fact.getSubject() + "\t" + fact.getPredicate() + "\t" + fact.getObject() + "\t" + json.get("defactoScore").toString());
                } catch (IOException e) {

                }

                if(json.get("complexProofs").toString().length() > 10){
                    int number = 1;
                    if(numberOfGeneratedFacts.containsKey(fact.getPredicate())){
                        // update
                        number = numberOfGeneratedFacts.get(fact.getPredicate());
                        number = number+1;
                    }
                    numberOfGeneratedFacts.put(fact.getPredicate(),number);
                }

                progress.put(key(fact),"done");
                updateProgress();
                SavehashMapinFile();
            }
        }
        private static void SavehashMapinFile(){
            FileOutputStream fileOut = null;
            try {
                fileOut =
                        new FileOutputStream(ProgressFileHashMap);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                System.out.println("updateHSM");
                out.writeObject(numberOfGeneratedFacts);
                out.close();
                fileOut.close();
                System.out.println("HSM size is :"+numberOfGeneratedFacts.size());
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



