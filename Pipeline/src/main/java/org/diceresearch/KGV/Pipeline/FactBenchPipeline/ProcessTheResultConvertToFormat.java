package org.diceresearch.KGV.Pipeline.FactBenchPipeline;

import net.minidev.json.parser.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.diceresearch.KGV.ETL.Transform.ToListTrensform;

import java.io.*;
import java.util.List;

public class ProcessTheResultConvertToFormat {

    /*static String refrenceFilePath = "/home/farshad/repos/factBench/factbench/test/wrong/mix/fb_mix.nt";
    static String resultFileToProccessPath ="/home/farshad/repos/factBench/factbench/test/VT_false_pathLen_2_textResults.tsv";
    static String resultFileFinalPath = "/home/farshad/repos/factBench/factbench/test/wrong/mix/RESULT_VT_false_pathLen_2_wrong.nt";*/

/*
    static String refrenceFilePath = "/home/farshad/repos/factBench/factbench/test/wrong/domain/groundTruth6predicatesFalseDomain.nt";
    static String resultFileToProccessPath ="/home/farshad/repos/factBench/factbench/test/wrong/domain/useTentris0.5_Counter_PreProcess_PathScore_NPMI_ScoreSummarist_AdaptedRootMeanSquareSummarist_textResults.tsv";//ERR_IDS_VT_false_pathLen_3_textResults
    static String resultFileFinalPath = "/home/farshad/repos/factBench/factbench/test/wrong/domain/useTentris0.5_Counter_PreProcess_PathScore_NPMI_ScoreSummarist_AdaptedRootMeanSquareSummarist_textResults.nt";
*/

    //static String resultFileErrorIds = "/home/farshad/repos/factBench/factbench/test/wrong/property/ErrorIDS.nt";





/*
    static String refrenceFilePath = "/home/farshad/repos/factBench/factbench/test/correct/groundTruth6predicatesTrue.nt";
    static String resultFileToProccessPath ="/home/farshad/repos/factBench/factbench/test/correct/useTentris0.5_Counter_PreProcess_PathScore_NPMI_ScoreSummarist_AdaptedRootMeanSquareSummarist_textResults.tsv";
    static String resultFileFinalPath = "/home/farshad/repos/factBench/factbench/test/correct/useTentris0.5_Counter_PreProcess_PathScore_NPMI_ScoreSummarist_AdaptedRootMeanSquareSummarist_textResults.nt";
*/


    static String refrenceFilePath = "/home/farshad/Desktop/preproccessResult/groundTruthFoundationPlace.nt";

    static String resultFileToProccessPath ="/home/farshad/repos/factBench/factbench/test/correct/useTentris0.5_Counter_PreProcess_PathScore_NPMI_ScoreSummarist_AdaptedRootMeanSquareSummarist_textResults.tsv";
    static String resultFileFinalPath = "/home/farshad/Desktop/preproccessResult/FoundationPlace/0.5/FoundationPlaceTrue.nt";

/*    static String resultFileToProccessPath ="/home/farshad/repos/factBench/factbench/test/wrong/domain/useTentris0.9_Counter_PreProcess_PathScore_NPMI_ScoreSummarist_AdaptedRootMeanSquareSummarist_textResults.tsv";
    static String resultFileFinalPath = "/home/farshad/Desktop/preproccessResult/FoundationPlace/0.9/FoundationPlaceFalse.nt";*/

    // use refrence file to find the ID
    // generate result finle in a format
    public static void main(String[] args) throws Exception {
       Run();
    }

    public static void Run() throws Exception {

        IExtractor extractor = new FileExtractorOutOfPackage();
        // load refrence file
        File refrence = (File) extractor.Extract(refrenceFilePath);
        RDFModelTransform rdfModelTransform = new RDFModelTransform("nt");
        Model model= rdfModelTransform.Transform(refrence,"");

        // read result file
        File result = (File) extractor.Extract(resultFileToProccessPath);
        ToListTrensform transform = new ToListTrensform();
        List<List<String>> listForConvert = transform.Transform(result,"\t");

        // convert

        for(List<String> forConvert : listForConvert){
            //because the file contain more than mix results
/*            if(!forConvert.get(0).contains("mix")){
                continue;
            }*/


            String subject = forConvert.get(1);
            String predicate = forConvert.get(2);
            String object = forConvert.get(3);
            Double score = Double.parseDouble(forConvert.get(4));

            if(!predicateIsValid(predicate)){
                continue;
            }

            String id = searchId(model,subject,predicate,object);

            if(id.equals("0")){
                /*try(FileWriter fw = new FileWriter(resultFileErrorIds, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw))
                {
                    out.println(forConvert.get(0)+",");
                } catch (IOException e) {

                }*/
                throw new Exception(forConvert.get(0));
            }
            else {
                writeResult(id, subject, predicate, object, score,resultFileFinalPath);
            }
        }
    }

    private static boolean predicateIsValid(String predicate) {
        /*if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/birthPlace")){
            return true;
        }
       if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/deathPlace")){
            return true;
}*/
        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/foundationPlace")){
            return true;
        }/*
        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/spouse")){
            return true;
        }

        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/starring")){
            return true;
        }

        if(predicate.equalsIgnoreCase("http://dbpedia.org/ontology/subsidiary")){
            return true;
        }*/

        return false;
    }

    public static String searchId(Model model, String subject, String predicate, String object) throws Exception {

        String query = "select ?a WHERE { " +
                "?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <"+object.replace("%26","&")+">." +
                "?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <"+predicate+">." +
                "?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> <"+subject.replace("%26","&")+">." +
                "}";

        ResultSet result = QueryExecutionFactory.create( query, model ).execSelect();

        if (result.hasNext()) {
            QuerySolution qs = result.next();

            return qs.get("?a").toString();
        }

        //throw new Exception(" Can not find ID subject"+subject+" predicate "+ predicate + " object "+ object);
        System.out.println(" Can not find ID subject"+subject+" predicate "+ predicate + " object "+ object);
        return "0";
    }

    //<http://swc2017.aksw.org/task2/dataset/fb_mix-1914> <http://swc2017.aksw.org/hasTruthValue> "1.0"^^<http://www.w3.org/2001/XMLSchema#double> .
    //<http://swc2017.aksw.org/task2/dataset/fb_mix-1914> <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> <http://dbpedia.org/resource/Stardock> .
    //<http://swc2017.aksw.org/task2/dataset/fb_mix-1914> <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://dbpedia.org/ontology/foundationPlace> .
    //<http://swc2017.aksw.org/task2/dataset/fb_mix-1914> <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://dbpedia.org/resource/Livonia,_Michigan> .
    //<http://swc2017.aksw.org/task2/dataset/fb_mix-1914> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
    public static void writeResult(String id, String subject, String predicate, String object, Double score,String outPutPath) {
        StringBuilder sb = new StringBuilder();

        id = "<"+id+">";

        sb.append(id);
        sb.append(" <http://swc2017.aksw.org/hasTruthValue> \""+score+"\"^^<http://www.w3.org/2001/XMLSchema#double> .");
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

        try(FileWriter fw = new FileWriter(outPutPath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(sb.toString());
        } catch (IOException e) {

        }
    }

}
