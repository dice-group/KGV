package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Load.FileLoader;
import org.diceresearch.KGV.ETL.Model.SimpleLabeledRDF;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

// read data and convert to dbpedia form
public class YAGO3Pipeline {
    public static void main(String[] args) throws Exception {
        Run();
    }
    public static void Run() throws Exception {
        String _yagoDBpediaInstances="data/yagoDBpediaInstances.tsv";
        String _1000File = "data/1000FileYago3.txt";
        String _yago3toDBpedia = "data/_Yago3toDBpedia.nt";

        IExtractor extractor = new FileExtractor();

        File yagoDBpediaInstances = (File) extractor.Extract(_yagoDBpediaInstances);
        File rdf4Check = (File) extractor.Extract(_1000File);
        File yago3toDBpedia = (File) extractor.Extract(_yago3toDBpedia);

        SimpleRdfRandomIdTransform transform = new SimpleRdfRandomIdTransform();

        ToHashMapTransform equivalenceTransformer = new ToHashMapTransform();

        //List<SimpleRDF> labeldYago = transform.Transform(yagoDBpediaInstances,"\t");
        List<SimpleRDF> forCheck = transform.Transform(rdf4Check," ");
        List<SimpleRDF> Yago2dbpediaPredicatesAsRDF = transform.Transform(yago3toDBpedia,"\t");

        HashMap<String,String> Yago2dbpediaPredicates = equivalenceTransformer.Transform(Yago2dbpediaPredicatesAsRDF,"");

        //Convert 1000 file to dbpedia rdf
        ToDBpediaTransform dBpediaTransformer = new ToDBpediaTransform(Yago2dbpediaPredicates);
        List<SimpleRDF> forCheckDbpediaVersion = dBpediaTransformer.Transform(forCheck,"");

        FileLoader loader = new FileLoader();

        loader.Save(forCheckDbpediaVersion,"Converted1000FileYago3.txt");

    }
}
