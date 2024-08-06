package org.diceresearch.KGV.ETL.Utility;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PredicateMapper {

    final
    FileExtractor extractor;

    final
    RDFModelTransform transform;

    Model model;

    public PredicateMapper(FileExtractor extractor, RDFModelTransform transform) throws Exception {
        this.extractor = extractor;
        this.transform = transform;
        File file = extractor.Extract("./data/shapes.ttl");
        model = transform.transform(file,"") ;
    }

    public String ToWikiData(String fromYago){
        return "";
    }

    public Model getModel() {
        return model;
    }
}
