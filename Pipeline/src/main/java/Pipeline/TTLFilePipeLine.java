package Pipeline;

import Extract.FileExtractor;
import Extract.IExtractor;
import Model.SimpleLabeledRDF;
import Transform.BeliefTransform;
import Transform.CorrectnessTransform;
import Transform.ITransform;
import Transform.RDFModelTransform;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class TTLFilePipeLine {
    public static void main(String[] args) throws Exception {
        Run();
    }
    public static void Run() throws Exception {
        String ttlFilePath = "data/yago-2.ttl";

        IExtractor extractor = new FileExtractor();

        File ttlFile = (File) extractor.Extract(ttlFilePath);


        RDFModelTransform rdfTransform = new RDFModelTransform();

        Model model = rdfTransform.Transform(ttlFile, "");

        System.out.println("size is :"+model.size());

    }
}
