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
import java.util.List;
import java.util.Set;

public class ProcessDBPediaDumpDataPipeline {
    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws Exception {
        String OntologyFileAdress="/home/farshad/Downloads/Persepolis/Others/dbo-snapshots.owl";
        String ttlFileAdress="/home/farshad/repos/dbpediadump/mappingbased-objects_lang=en_disjointDomain.ttl";
        String newTtlFileAdress="/home/farshad/repos/dbpediadump/mappingbased-objects_lang=en_disjointDomain_NEW.ttl";

        IExtractor extractor = new FileExtractorOutOfPackage();

        File ontologyFile = (File) extractor.Extract(OntologyFileAdress);
        File ttlFile = (File) extractor.Extract(ttlFileAdress);

        ITransform<OntModel, File> ontologyTransforme = new OntologyModelTransform();
        ITransform<Model, File> ttlModel = new RDFModelTransform();

        OntModel ontology = ontologyTransforme.Transform(ontologyFile,null);
        Model model = ttlModel.Transform(ttlFile,null);

        //System.out.println(ontology.size());
        System.out.println(model.size());

        ExplicitType utility = new ExplicitType();

        List<Statement> stmts = model.listStatements().toList();
        for (Statement curStatement : stmts) {
            Set<Statement> newStmts = utility.searchType(curStatement, ontology, model);
            model.add(newStmts.toArray(new Statement[newStmts.size()]));
        }

        System.out.println(model.size());

        Model2ttlLoader loader = new Model2ttlLoader();
        loader.Save(model,newTtlFileAdress);

    }
}
