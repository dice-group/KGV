package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.CopaalOutPutTransform;

import java.io.File;
import java.util.List;

public class countallpredicatescountsinfile {
    private static String fileToProcess ="/home/farshad/experiments/yago2Copaal/Results/Beliefs/3resultIsValid_FactCheck_yagoBeliefs2isLocatedIn_01092022_NewModel_SMOJ48_partialTrim.txt";
    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws Exception {
        IExtractor extractor = new FileExtractorOutOfPackage();

        File outPutOfCopaal = (File) extractor.Extract(fileToProcess);
        CopaalOutPutTransform transformer = new CopaalOutPutTransform();
        transformer.SetCopaalOutPutTransform(0.1);
        List<SimpleRDF> forCheck = transformer.transform(outPutOfCopaal, "\t");
        int trueFacts = 0;
        for(SimpleRDF rdf: forCheck){
            if(rdf.getCorrectness()){
                trueFacts++;
            }
        }

        System.out.println("total triples : "+ forCheck.size()+" true detected : "+trueFacts+" false detected "+ (forCheck.size() - trueFacts));
    }
}
