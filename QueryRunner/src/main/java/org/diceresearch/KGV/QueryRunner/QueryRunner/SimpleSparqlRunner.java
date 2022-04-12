package org.diceresearch.KGV.QueryRunner.QueryRunner;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

public class SimpleSparqlRunner {
    String Endpoint;
    public SimpleSparqlRunner(String endpoint){
        this.Endpoint = endpoint;
    }

    public QueryExecution getQueryExecution(Query query){
        return QueryExecutionFactory.createServiceRequest(Endpoint, query);
    }

}
