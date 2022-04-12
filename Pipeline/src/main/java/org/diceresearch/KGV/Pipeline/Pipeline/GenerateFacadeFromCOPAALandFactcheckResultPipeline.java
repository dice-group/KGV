package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Transform.TabSeperetadResultToMapTransform;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
/*
 read text result of coppal and fact check with this format
<https://dbpedia.org/resource/Wenceslas_Lauret>	<http://dbpedia.org/ontology/birthPlace>	<https://dbpedia.org/resource/Tarbes>	0.9972936828287049
then convert and save in file with this format
<https://dbpedia.org/resource/Michael_Moriarty>	<http://dbpedia.org/ontology/award>	<https://dbpedia.org/resource/Tony_Award>	0.9845335032459794	0.38461538461538464	0.9845335032459794
the first score is copaal score second one is factcheck and the last is facade score
*/

public class GenerateFacadeFromCOPAALandFactcheckResultPipeline {

    static int pathLen = 2;
    static String predicate = "location";
    static boolean isVirtual = true;

    static String adrCOPAAL ="/home/farshad/repos/KGV/Results";
    static String sssCOPAAL = predicate+"_VT_"+isVirtual+"_pl_"+pathLen;
    static String FileNameValidCOPAAL = adrCOPAAL+"/resultIsValid_"+sssCOPAAL+".txt";

    static String adrFactCheck ="/home/farshad/repos/KGV/ResultsFactCheck";
    static String sssFactCheck = predicate+"_FactCheck";
    static String FileNameValidFactCheck = adrFactCheck+"/resultIsValid_FactCheck_"+sssFactCheck+".txt";

    static String adrFacade ="/home/farshad/repos/KGV/ResultsFacade";
    static String sssFacade = predicate+"_VT_"+isVirtual+"_pl_"+pathLen;

    static String FileNameValidFacade = adrFacade+"/Generated_resultIsValid_"+sssFacade+"_facade.txt";

    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws Exception {

        IExtractor extractor = new FileExtractorOutOfPackage();

        File copaalResult = (File) extractor.Extract(FileNameValidCOPAAL);
        File factCheckResult = (File) extractor.Extract(FileNameValidFactCheck);

        TabSeperetadResultToMapTransform transformer = new TabSeperetadResultToMapTransform();

        Map<String,Double> copaalMap  = transformer.Transform(copaalResult,"\t");
        Map<String,Double> factCheckMap = transformer.Transform(factCheckResult,"\t");

        if(copaalMap.size()!=factCheckMap.size()){
            throw new Exception("the size is not the same "+ FileNameValidCOPAAL+ " "+FileNameValidFactCheck);
        }

        int copaalC = 0,factcheckC = 0,facadeC = 0;

        for (Map.Entry<String, Double> entry : copaalMap.entrySet()) {
            Double copaalScore = entry.getValue();
            Double factCheckScore = factCheckMap.get(entry.getKey());
            Double facadeScore = Math.max(copaalScore,factCheckScore);

            if(copaalScore>0){copaalC++;}
            if(factCheckScore>0){factcheckC++;}
            if(facadeScore>0){facadeC++;}

            try(FileWriter fw = new FileWriter(FileNameValidFacade, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(entry.getKey()+"\t"+copaalScore+"\t"+factCheckScore+"\t"+facadeScore);
            } catch (IOException e) {
                throw e;
            }
        }

        System.out.println(copaalC+" "+factcheckC+" "+facadeC);
    }
}
