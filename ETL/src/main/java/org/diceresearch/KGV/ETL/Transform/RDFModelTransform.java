package org.diceresearch.KGV.ETL.Transform;

import java.io.File;
import java.io.FileInputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Component;

@Component
public class RDFModelTransform implements ITransform<Model, File>{
    @Override
    public Model Transform(File input, String splitter) throws Exception {
        try {
            Model model = ModelFactory.createDefaultModel();
            model.read(new FileInputStream(input), "", "Turtle");
            return model;
        }
        catch(Exception ex){
            String ddd = ex.getMessage();
        }
        return null;
    }
}
