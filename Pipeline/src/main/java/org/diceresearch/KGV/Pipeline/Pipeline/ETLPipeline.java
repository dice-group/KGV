package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleLabeledRDF;
import org.diceresearch.KGV.ETL.Transform.BeliefTransform;
import org.diceresearch.KGV.ETL.Transform.CorrectnessTransform;
import org.diceresearch.KGV.ETL.Transform.ITransform;

import java.io.File;
import java.util.HashMap;
import java.util.List;

// just transform the dataset from yago and nell which mentioned in the kgQ paper
public class ETLPipeline {
    public static void main(String[] args) throws Exception {
        Run();
    }
    public static void Run() throws Exception {
        String nellDatasetLabels="data/NELL_Mturk";
        String yagoDatasetLabels="data/YAGO_Mturk";

        String nellDataset="data/nellFacts";
        String yagoDataset="data/yagoFacts";

        IExtractor extractor = new FileExtractor();

        File NellDataset = (File) extractor.Extract(nellDataset);
        File YagoDataset = (File) extractor.Extract(yagoDataset);
        File NellDatasetLabels = (File) extractor.Extract(nellDatasetLabels);
        File YagoDatasetLabels = (File) extractor.Extract(yagoDatasetLabels);

        ITransform correctnessTransform = new CorrectnessTransform();

        BeliefTransform beliefTransform = new BeliefTransform();

        List<SimpleLabeledRDF>  labeldYago =
                beliefTransform.Merge(
                        (HashMap<String, Boolean>) correctnessTransform.Transform(YagoDatasetLabels,"\t"),
                        beliefTransform.Transform(YagoDataset,"\t"));

        List<SimpleLabeledRDF>  labeldNell =
                beliefTransform.Merge(
                        (HashMap<String, Boolean>) correctnessTransform.Transform(NellDatasetLabels,"\t"),
                        beliefTransform.Transform(NellDataset,"\t"));


    }
}
