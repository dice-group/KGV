package org.diceresearch.KGV.Pipeline.FactBenchPipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.diceresearch.KGV.utility.TripleExtractor;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class RunCopaalPipeline {

    static int pathLen = 3;
    static boolean isVirtual = false;

    static String key =  "VT_"+isVirtual+"_pathLen_"+pathLen+"_";

    static String url = "http://localhost:8282/api/v1/validate?";

    static String factBenchPath = "/home/farshad/repos/factBench/factbench/test/wrong/property";

    //static String factBenchPath = "/home/farshad/repos/factBench/factbench/test/correct";


    static String ProgressFileName = factBenchPath+"/"+key+"prf.txt";
    static String ProgressTextResults = factBenchPath+"/"+key+"textResults.txt";
    static String ProgressResults = factBenchPath+"/"+key+"textResults.tsv";

    static HashMap<String,String> progress = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Run();
        //checkOneFile("/home/farshad/repos/factBench/factbench/test/wrong/mix/date/foundationPlace/foundationPlace_00139.ttl");
    }

    public static void Run() throws IOException, ParseException {
        //if there is pr file
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

        checkFacts(factBenchPath);
    }

/*    public static void main(String[] args) {
        factBechTest d = new factBechTest();
        try {
            d.checkFacts(factBenchPath);
        }catch (Exception exp){
            System.out.println(exp);
        }
    }*/

    public static void checkOneFile(String path ) throws IOException, ParseException {
        File file = new File(path);
        //check fact
        String result = checkFactFromFile(file);
        // add Progress
        progress.put(file.getPath(),result);
        // write to file
        String FileName = "result_"+file.getName();
        try (PrintWriter out = new PrintWriter(file.getParent()+"/"+FileName+"r")) {
            out.println(result);
        }
        System.out.println(file.toPath());
        System.out.println(result);
        System.out.println("-=-=-=-=-=-=-==-=-==-=-=-=-==-=-=-=-=-=-=-==-=-=-=-");
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
                    try (PrintWriter out = new PrintWriter(file.getParent()+"/"+FileName+"r")) {
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
                        //exception handling left as an exercise for the reader
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

        if(isVirtual) {
            urlForCoppal = url + "subject=" + tripleExtractor.getSubjectUri().replace("&","%26") + "&object=" + tripleExtractor.getObjectUri().replace("&","%26") + "&property=" + tripleExtractor.getPredicateUri().replace("&","%26") + "&verbalize=False&virtualType=True+&pathlength="+pathLen;
        }else {
            urlForCoppal = url + "subject=" + tripleExtractor.getSubjectUri().replace("&","%26") + "&object=" + tripleExtractor.getObjectUri().replace("&","%26") + "&property=" + tripleExtractor.getPredicateUri().replace("&","%26") + "&verbalize=False&virtualType=False+&pathlength="+pathLen;
        }

        urlForCoppal.replace("https","http");

        respons = hrr.sendHTTPGetRequest(urlForCoppal);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(respons);
        JSONObject inputTriple = (JSONObject)json.get("inputTriple");

        try(FileWriter fw = new FileWriter(ProgressResults, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(input.getAbsolutePath()+"\t"+inputTriple.get("subject").toString()+"\t"+inputTriple.get("property").toString()+"\t"+inputTriple.get("object").toString()+"\t"+json.get("graphScore").toString());
        } catch (IOException e) {

        }

        /*progress.put(input.getPath(),json.get("graphScore").toString());
        updateProgress();*/

        return respons;
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
