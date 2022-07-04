package org.diceresearch.KGV.Pipeline.FactBenchPipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.diceresearch.KGV.utility.TripleExtractor;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * this class is runnable
 * get a folder , run each fact over Copaal and save the result
 *
 */

public class RunCopaalPipeline {

    //static String Counter = "ApproximatingCountRetriever";
    static String Counter = "PreProcess";

    static String PathScore="NPMI";
    //static String PathScore="PNPMI";

    static String ScoreSummarist="AdaptedRootMeanSquareSummarist";
    //static String ScoreSummarist="CubicMeanSummarist";
    //static String ScoreSummarist="FixedSummarist";    //FixedSummarist <-> SquaredAverageSummarist
    //static String ScoreSummarist="HigherOrderMeanSummarist";
    //static String ScoreSummarist="NegScoresHandlingSummarist";
    //static String ScoreSummarist="OriginalSummarist";
    //static String ScoreSummarist="SquaredAverageSummarist";



    //static String factBenchPath = "/home/farshad/repos/factBench/factbench/train/correct";
    static String factBenchPath = "/home/farshad/repos/BPDP_Dataset/Test/True";
    static String correctNess = "true";

    //static String factBenchPath = "/home/farshad/repos/factBench/factbench/train/wrong";
/*    static String factBenchPath = "/home/farshad/repos/BPDP_Dataset/Train/False";
    static String correctNess = "false";*/

/*    static String factBenchPath = "/home/farshad/repos/factBench/factbench/test/wrong/property";
    static String correctNess = "false"*/;

    //static String key =  "ERR_IDS_VT_"+isVirtual+"_pathLen_"+pathLen+"_";

    static String key =  "umair"+"_Counter_"+Counter+"_PathScore_"+PathScore+"_ScoreSummarist_"+ScoreSummarist+"_";

    // COPAAL endpoint
    static String url = "http://synthg-fact-leapfrog.cs.upb.de:80/api/v1/validate?";
    //static String url = "http://localhost:8080/api/v1/validate?";


    static String ProgressFileName = factBenchPath+"/"+key+"prf.txt";
    static String ProgressTextResults = factBenchPath+"/"+key+"textResults.txt";
    static String ProgressResults = factBenchPath+"/"+key+"textResults.tsv";
    static String JsonResult = factBenchPath+"/"+key+".json";
    static boolean itIsFirst;
    //static String AllTheResultTogether = "/home/farshad/repos/CopaalConfigTestResults/resultsTentrisPreProcesstesttime2.tsv";

    static HashMap<String,String> progress = new HashMap<>();

    public static void main(String[] args) throws Exception {
        itIsFirst = true;
        Run();
        //checkOneFile("/home/farshad/repos/factBench/factbench/test/wrong/property/subsidiary/subsidiary_00077.ttl");
        //runForErrorIds();
    }

    private static void runForErrorIds() throws IOException, ClassNotFoundException, ParseException {
        String adrFileErrorIds = "/home/farshad/repos/factBench/factbench/test/wrong/property/ErrorIDS.nt";
        FileExtractorOutOfPackage ex = new FileExtractorOutOfPackage();
        File f = ex.Extract(adrFileErrorIds);
        Scanner scanner = new Scanner(f);
        String line = "";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String result = checkOneFile(line);
        }

        scanner.close();
    }

    public static void Run() throws IOException, ParseException {
        //if there is pr file
        /*File prf = new File(ProgressFileName);
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
        }*/

        checkFacts(factBenchPath);
        // add last token for json
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(JsonResult, true)));
            out.println("]}");
            out.close();
        }catch (IOException e) {

        }
    }

/*    public static void main(String[] args) {
        factBechTest d = new factBechTest();
        try {
            d.checkFacts(factBenchPath);
        }catch (Exception exp){
            System.out.println(exp);
        }
    }*/

    public static String checkOneFile(String path ) throws IOException, ParseException {
        File file = new File(path);
        //check fact
        String result = checkFactFromFile(file);
        // add Progress
        progress.put(file.getPath(),result);
        // write to file
        /*String FileName = "result_"+file.getName();
        try (PrintWriter out = new PrintWriter(file.getParent()+"/"+FileName+"r")) {
            out.println(result);
        }*/
        System.out.println(file.toPath());
        System.out.println(result);
        System.out.println("-=-=-=-=-=-=-==-=-==-=-=-=-==-=-=-=-=-=-=-==-=-=-=-");
        return result;
    }

    public static void checkFacts(String path) throws IOException, ParseException {

        File dir = new File(path);
        File[] files = dir.listFiles();
        for (File file : files) {
            if(file.isFile()){
                System.out.println(FilenameUtils.getExtension(String.valueOf(file)));
                if(FilenameUtils.getExtension(String.valueOf(file)).equals("ttl")){
                    System.out.println("Check This File"+file.getPath());

                    // check if it is checked before
                    if(progress.containsKey(file.getPath())){
                        continue;
                    }

                    //check fact
                    String result = checkFactFromFile(file);
                    // add Progress
                    progress.put(file.getPath(),result);
                    // write to file
                    String FileName = "result_"+file.getName();
                    try (PrintWriter out = new PrintWriter(file.getParent()+"/"+FileName+".resultCopalConfig_"+key)) {
                        out.println(result);
                    }
                    // update progress
                    updateProgress();

                    try {

                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ProgressTextResults, true)));

                        out.println(file.toPath());
                        out.println(result);
                        out.println("-=-=-=-=-=-=-==-=-==-=-=-=-==-=-=-=-=-=-=-==-=-=-=-");
                        out.close();
                    }catch (IOException e) {
                        System.out.println(e.getMessage());
                    }


                    // write result in one File

                    /*try {

                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(AllTheResultTogether, true)));


                        LocalDate time = LocalDate.now(); // Create a date object
                        String toPrint =time.toString()+"\t"+Counter+"\t"+PathScore+"\t"+ScoreSummarist+"\t"+correctNess+"\t"+result;
                        out.println(toPrint);
                        out.close();
                    }catch (IOException e) {
                        System.out.println(e.getMessage());
                    }*/

                    // write as json file

                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(JsonResult, true)));
                        if(itIsFirst==true){
                            itIsFirst = false;
                            out.println("{\"results\":[");
                        }else{
                            out.println(",");
                        }
                        out.println("{");
                        out.println("\"filename\": \""+file.toPath()+"\",");
                        out.println("\"result\": "+result.split("\t")[3]);
                        out.println("}");
                        out.close();
                    }catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                }
            }
            if(file.isDirectory()){
                String newPath = path +"/"+file.getName();
                checkFacts(newPath);
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

    private static String checkFactFromFile(File input) throws IOException, ParseException {

        String fileData = ReadFile(input);

        TripleExtractor tripleExtractor = new TripleExtractor(fileData, false);


        String urlForCoppal = "";
        String respons="";

        HttpRequestRunner hrr = new HttpRequestRunner(new RestTemplateBuilder());

        urlForCoppal = url + "subject=" + tripleExtractor.getSubjectUri().replace("&","%26") + "&object=" + tripleExtractor.getObjectUri().replace("&","%26") + "&property=" + tripleExtractor.getPredicateUri().replace("&","%26") + "&verbalize=False";

        urlForCoppal.replace("https","http");

        respons = hrr.sendHTTPGetRequest(urlForCoppal);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(respons);
        //JSONObject inputTriple = (JSONObject)json.get("inputTriple");

        try(FileWriter fw = new FileWriter(ProgressResults, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(input.getAbsolutePath()+"\t"+tripleExtractor.getSubjectUri().replace("&","%26")+"\t"+tripleExtractor.getPredicateUri().replace("&","%26")+"\t"+tripleExtractor.getObjectUri().replace("&","%26")+"\t"+json.get("veracityValue").toString());
        } catch (IOException e) {
String m = e.getMessage();
        }

        /*progress.put(input.getPath(),json.get("graphScore").toString());
        updateProgress();*/

        return tripleExtractor.getSubjectUri().replace("&","%26")+"\t"+tripleExtractor.getPredicateUri().replace("&","%26")+"\t"+tripleExtractor.getObjectUri().replace("&","%26")+"\t"+respons;
    }

    private static String ReadFile(File input) throws FileNotFoundException {
        Scanner myReader = new Scanner(input);
        StringBuilder sb = new StringBuilder();
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            sb.append(data);
        }
        myReader.close();
        return sb.toString();
    }


}
