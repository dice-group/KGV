package org.diceresearch.KGV.Pipeline.Pipeline;

import net.minidev.json.parser.ParseException;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;

import java.io.*;
import java.util.Scanner;

public class GetClinicalTrailsResultGenerateGroundTruthAndFilesForGerbil {

    static String inputFilePath ="/home/farshad/experiments/ClinicaltrailsResults/results07122022/0shortResults_trueTriplesnot.txt";
    static boolean isTrue = true;

    static String outPutDestination = "/home/farshad/experiments/ClinicaltrailsResults/results07122022/out_+"+isTrue+"_calculated";
    static String outPutDestinationGround = "/home/farshad/experiments/ClinicaltrailsResults/results07122022/out_+"+isTrue+"_ground";

    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws IOException, ParseException, ClassNotFoundException {
        FileExtractorOutOfPackage extractor = new FileExtractorOutOfPackage();
        File input = extractor.Extract(inputFilePath);
        Scanner scanner = new Scanner(input);
        String line = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] words = line.split("\t");
            String subject = words[0];
            String predicate = words[1];
            String object = words[2];
            String veracity = words[3];
            String id = words[4];
            writeResult(id,subject,predicate,object,veracity);
        }
    }

    private static void writeResult(String id, String subject, String predicate, String object, String veracity) throws IOException {
        StringBuilder sb = new StringBuilder();

        id = "<http://swc2017.aksw.org/task2/dataset/"+id+">";

        sb.append(id);

            sb.append(" <http://swc2017.aksw.org/hasTruthValue> \""+veracity+"\"^^<http://www.w3.org/2001/XMLSchema#double> .");
            sb.append(System.lineSeparator());

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

        sb = new StringBuilder();

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

        try(FileWriter fw = new FileWriter(outPutDestinationGround, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(sb.toString());
        } catch (IOException e) {
            throw e;
        }
    }
}
