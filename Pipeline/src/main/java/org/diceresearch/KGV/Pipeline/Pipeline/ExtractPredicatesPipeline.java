package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.SimpleRdfRandomIdTransform;
import org.diceresearch.KGV.ETL.Transform.ToSetTransform;

import java.io.File;
import java.util.List;
import java.util.Set;

public class ExtractPredicatesPipeline {
    public static void main(String[] args) throws Exception {
        Run();
    }
    public static void Run() throws Exception {
        String _facts="data/yagoFacts.nt";

        IExtractor extractor = new FileExtractor();

        File facts = (File) extractor.Extract(_facts);

        ToSetTransform transform = new ToSetTransform();

        Set<String> labeldYago = transform.Transform(facts," ");

        System.out.println("Size is:"+labeldYago.size());

        for(String p : labeldYago){
            System.out.println(p);
        }
    }
}
