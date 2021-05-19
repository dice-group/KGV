package org.diceresearch.KGV.ETL.Utility;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.diceresearch.KGV.Aplication.SampleAPP;
import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SampleAPP.class)
public class PredicateMapperTest {

    @Test
    public void ConstructorISWorkFine() throws Exception {
        PredicateMapper p = new PredicateMapper(new FileExtractor(),new RDFModelTransform());
        Model model = p.getModel();
        Assertions.assertNotEquals(model.size(),0);
    }

    @Test
    public void DoQuery() throws Exception {
        PredicateMapper p = new PredicateMapper(new FileExtractor(),new RDFModelTransform());
        Model model = p.getModel();

        String query = "select DISTINCT ?o WHERE { " +
                "?s <http://www.w3.org/ns/shacl#path> <http://schema.org/knowsLanguage>." +
                "?s <http://yago-knowledge.org/schema#fromProperty> ?o." +
                "} LIMIT 100";

        System.out.println( "\n=== SPARQL results ===" );
        ResultSetFormatter.out( QueryExecutionFactory.create( query, model ).execSelect() );
    }
}

//<http://yago-knowledge.org/schema#fromProperty>


/*@prefix sh: <http://www.w3.org/ns/shacl#> .
@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
@prefix schema: <http://schema.org/> .*/
    /*sh:property [
            sh:path schema:knowsLanguage ;
            sh:node schema:Language ;
            ys:fromProperty wdt:P1412 ;
            ] ;
*/