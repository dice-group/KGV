package org.diceresearch.KGV.Pipeline.Pipeline;

// this class accept results of fact check and copaal for a set also a subset of that set then extract the checked fact and save them in a file also
// a report of triples which exist in subset but not checked in a copaal and fact check results

import org.apache.jena.rdf.model.Model;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Model.SimpleRDFWithVeracity;
import org.diceresearch.KGV.ETL.Transform.CopaalOutPutTransform;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.diceresearch.KGV.ETL.Transform.TTLtoSimpleRDFTransform;
import org.diceresearch.KGV.ETL.Transform.ToHashMapSelfKeyTransform;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class SetSubsetMap {
    static String copaalResultPath = "/home/farshad/experiments/yago2Copaal/final/copaal/all.txt";

    static String factcheckResultPath = "/home/farshad/experiments/yago2Copaal/final/FactCheck/allFactcheck.txt";

    static String subsetPath = "/home/farshad/repos/KGV/KGEvalData/YAGO/yago_subset.ttl";

    static String folderToSaveTheResults="/home/farshad/experiments/yago2Copaal/subsetResultRegenarated/ver3/";
    public static void main(String[] args) throws Exception {
        process();
    }
    public static void process() throws Exception {
        IExtractor extractor = new FileExtractorOutOfPackage();
        CopaalOutPutTransform copaalOutPutTransform = new CopaalOutPutTransform();
        TTLtoSimpleRDFTransform ttlTransformer = new TTLtoSimpleRDFTransform();
        RDFModelTransform rdfTransform = new RDFModelTransform();
        ToHashMapSelfKeyTransform mapper = new ToHashMapSelfKeyTransform();

        List<SimpleRDFWithVeracity> copaal = copaalOutPutTransform.TransformWithVeracity((File)extractor.Extract(copaalResultPath),"\t");
        HashMap<String, Double> copaalHM =  mapper.transform(copaal,"");

        List<SimpleRDFWithVeracity> factcheck = copaalOutPutTransform.TransformWithVeracity((File)extractor.Extract(factcheckResultPath),"\t");
        HashMap<String, Double> factcheckHM =  mapper.transform(factcheck,"");

        Model model = rdfTransform.transform((File)extractor.Extract(subsetPath), "");
        List<SimpleRDF> subset = ttlTransformer.transform(model,"");

        for(SimpleRDF rdf : subset){
            String key = mapper.getKey(rdf);
            // check if it is in the results of copaal print it
            if(copaalHM.containsKey(key)){
             // what is the result ?
                 if(copaalHM.get(key) > 0){
                     // write in the file of copaal
                     writeToFile("copaal.txt",key+copaalHM.get(key));
                 }else{
                     writeToFile("copaal.txt",key+"0.0");
                     // check fact check
                     if(factcheckHM.containsKey(key)){
                         // write in the file of factcheck
                         writeToFile("factcheck.txt",key+copaalHM.get(key));
                     }else{
                         // log in the file to do the check again
                         writeToFile("check_factcheck.txt",key+copaalHM.get(key));
                     }
                 }
            }else{
                // log in file for do the check again
                writeToFile("check_copaal.txt",key+copaalHM.get(key));
                writeToFile("check_factcheck.txt",key+"0.0");
            }
        }

        System.out.println(subset.size());
    }

    private static void writeToFile(String whatFile ,String what){
        try (FileWriter fw = new FileWriter(folderToSaveTheResults+whatFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(what);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
