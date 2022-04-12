package org.diceresearch.KGV.Pipeline.FactBenchPipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;
import org.diceresearch.KGV.utility.TripleExtractor;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.io.*;
import java.util.Scanner;

public class GenerateGroundTruthFile {

    static String predicate = "Subsidiary";

    static String outPutDestination = "/home/farshad/repos/factBench/factbench/test/correct/groundTruth"+predicate+".nt";
    static String startCrawlPath = "/home/farshad/repos/factBench/factbench/test/correct";
    // use as prefix for each id then follow by a number
    static String idKey = "true-";
    static boolean isTrue = true;

/*    static String outPutDestination = "/home/farshad/repos/factBench/factbench/test/wrong/domain/groundTruth"+predicate+".nt";
    static String startCrawlPath = "/home/farshad/repos/factBench/factbench/test/wrong/domain";
    // use as prefix for each id then follow by a number
    static String idKey = "domain-";
    static boolean isTrue = false;*/

    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws IOException, ParseException {
        int id = crawl(startCrawlPath,1000);
    }

    private static int crawl(String path,int id) throws IOException, ParseException {
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (File file : files) {
            if(file.isFile()){
                if(FilenameUtils.getExtension(String.valueOf(file)).equals("ttl")){
                    System.out.println(id);
                    id = id + 1;
                    //check fact
                    TripleExtractor triples = checkFactFromFile(file);

                    String idPhrase = idKey+id;
                    String subject = triples.getSubjectUri();
                    String predicate = triples.getPredicateUri();
                    if(!predicateIsValid(predicate)){
                        continue;
                    }
                    String object = triples.getObjectUri();

                    // write to file
                    writeResult(idPhrase,subject,predicate,object);

                }
            }
            if(file.isDirectory()){
                String newPath = path +"/"+file.getName();
                id = crawl(newPath,id);
            }
        }
        return id;
    }
    private static TripleExtractor checkFactFromFile(File input) throws IOException, ParseException {

        String fileData = ReadFile(input);

        TripleExtractor triples = new TripleExtractor(fileData, false);

        return triples;
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

    private static void writeResult(String id, String subject, String predicate, String object) throws IOException {
        StringBuilder sb = new StringBuilder();

        id = "<http://swc2017.aksw.org/task2/dataset/"+id+">";

        sb.append(id);
        if(isTrue){
            sb.append(" <http://swc2017.aksw.org/hasTruthValue> \"1.0\"^^<http://www.w3.org/2001/XMLSchema#double> .");
            sb.append(System.lineSeparator());
        }else{
            sb.append(" <http://swc2017.aksw.org/hasTruthValue> \"0.0\"^^<http://www.w3.org/2001/XMLSchema#double> .");
            sb.append(System.lineSeparator());
        }
        //line 2
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ");
        sb.append("<"+subject+"> .");
        sb.append(System.lineSeparator());

        //line 3
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> ");
        sb.append("<"+predicate+"> .");
        sb.append(System.lineSeparator());

        //line 4
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> ");
        sb.append("<"+object+"> .");
        sb.append(System.lineSeparator());

        //line 5
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .");

        try(FileWriter fw = new FileWriter(outPutDestination, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(sb.toString());
        } catch (IOException e) {
            throw e;
        }
    }

    private static boolean predicateIsValid(String predicate) {
/*        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/birthPlace")){
            return true;
        }
        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/deathPlace")){
            return true;
        }
        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/foundationPlace")){
            return true;
        }
        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/spouse")){
            return true;
        }
        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/starring")){
            return true;
        }*/
        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/subsidiary")){
            return true;
        }
        return false;
    }

}
