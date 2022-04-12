package org.diceresearch.KGV.ETL.Transform;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.FileInputStream;

public class OntologyModelTransform implements ITransform<OntModel, File>{
    @Override
    public OntModel Transform(File input, String splitter) throws Exception {
        try {
            OntModel model = ModelFactory.createOntologyModel();
            model.read(new FileInputStream(input), "", null);
            return model;
        }
        catch(Exception ex){
            String ddd = ex.getMessage();
        }
        return null;
    }
}