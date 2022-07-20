package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleLabeledRDF;
import org.diceresearch.KGV.ETL.Transform.BeliefTransform;
import org.diceresearch.KGV.ETL.Transform.CorrectnessTransform;
import org.diceresearch.KGV.ETL.Transform.ITransform;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class TTLFilePipeLine {
    public static void main(String[] args) throws Exception {
        Run();
    }
    public static void Run() throws Exception {
        String ttlFilePath = "/home/farshad/repos/KGV/KGEvalData/YAGO/yago_subset.ttl";

        IExtractor extractor = new FileExtractorOutOfPackage();

        File ttlFile = (File) extractor.Extract(ttlFilePath);


        RDFModelTransform rdfTransform = new RDFModelTransform();

        Model model = rdfTransform.Transform(ttlFile, "");

        System.out.println("size is :"+model.size());

    }
}
