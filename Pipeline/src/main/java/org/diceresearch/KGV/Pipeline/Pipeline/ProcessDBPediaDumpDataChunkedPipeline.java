package org.diceresearch.KGV.Pipeline.Pipeline;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Load.Model2ttlLoader;
import org.diceresearch.KGV.ETL.Transform.ITransform;
import org.diceresearch.KGV.ETL.Transform.OntologyModelTransform;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.diceresearch.KGV.ETL.Utility.ExplicitType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProcessDBPediaDumpDataChunkedPipeline {
    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws Exception {
        String OntologyFileAdress = "/home/farshad/Downloads/Persepolis/Others/dbo-snapshots.owl";
        String ttlFolderAdress = "/home/farshad/repos/dbpediadumpchunks/sameAs_uris=en";
        String newTtlFileAdress = "/home/farshad/repos/dbpediadump/testfile.ttl";

        IExtractor extractor = new FileExtractorOutOfPackage();

        File ontologyFile = (File) extractor.Extract(OntologyFileAdress);

        ITransform<OntModel, File> ontologyTransforme = new OntologyModelTransform();
        ITransform<Model, File> ttlModel = new RDFModelTransform();

        OntModel ontology = ontologyTransforme.Transform(ontologyFile, null);

        ExplicitType utility = new ExplicitType();


        List<File> files = Files.walk(Paths.get(ttlFolderAdress))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        Model model = ModelFactory.createDefaultModel();


        for(File f : files){
            Model temp = ttlModel.Transform(f, null);

            List<Statement> stmts = temp.listStatements().toList();
            for (Statement curStatement : stmts) {
                Set<Statement> newStmts = utility.searchType(curStatement, ontology, model);
                model.add(newStmts.toArray(new Statement[newStmts.size()]));
            }
            System.out.println("model size is :"+model.size());
        }

        Model2ttlLoader loader = new Model2ttlLoader();
        loader.Save(model, newTtlFileAdress);

    }
}
