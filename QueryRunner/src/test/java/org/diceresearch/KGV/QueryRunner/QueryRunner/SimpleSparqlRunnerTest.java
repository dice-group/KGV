package org.diceresearch.KGV.QueryRunner.QueryRunner;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimpleSparqlRunnerTest {
    @Test
    public void SimpleTestShouldRun(){
        SimpleSparqlRunner queryExecutioner = new SimpleSparqlRunner("http://localhost:3030/myGraph/query");


        ParameterizedSparqlString paraPathQuery =
                new ParameterizedSparqlString("SELECT ?s ?p ?o\n" +
                        "WHERE { ?s ?p ?o }\n" +
                        "LIMIT 100 OFFSET 120000");

        try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
            ResultSet result = qe.execSelect();

            StringBuilder sb = new StringBuilder();

            while (result.hasNext()) {
                QuerySolution qs = result.next();

                sb.append(" subject is : "+qs.get("?s"));
                sb.append(" predicate is : "+qs.get("?p"));
                sb.append(" object is : "+qs.get("?o"));
                sb.append(" |||||||||| ");
            }

            System.out.println(sb.toString());
        }catch (Exception ex){
            throw ex;
        }
    }


    @Test
    public void SelectAllPredicateAndObjectForASubject(){
        SimpleSparqlRunner queryExecutioner = new SimpleSparqlRunner("http://localhost:3030/myGraph/query");


        ParameterizedSparqlString paraPathQuery =
                new ParameterizedSparqlString("SELECT ?p ?o\n" +
                        "WHERE { <http://yago-knowledge.org/resource/Bill_Gates> ?p ?o }\n" +
                        "LIMIT 100");

        try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
            ResultSet result = qe.execSelect();

            StringBuilder sb = new StringBuilder();

            while (result.hasNext()) {
                QuerySolution qs = result.next();

                sb.append(" predicate is : "+qs.get("?p"));
                sb.append(" object is : "+qs.get("?o"));
                sb.append(" |||||||||| ");
            }

            System.out.println(sb.toString());
        }catch (Exception ex){
            throw ex;
        }
    }


    @Test
    public void CountSubjectAndObjects(){
        SimpleSparqlRunner queryExecutioner = new SimpleSparqlRunner("http://localhost:3030/myGraph/query");


        ParameterizedSparqlString paraPathQuery =
                new ParameterizedSparqlString("SELECT  (count(*) AS ?c)"+
        "WHERE"+
        "{ ?s  <http://yago-knowledge.org/resource/livesIn>  ?o}");

        try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
            ResultSet result = qe.execSelect();

            StringBuilder sb = new StringBuilder();

            while (result.hasNext()) {
                QuerySolution qs = result.next();

                sb.append(" c is : "+qs.get("?c"));
                sb.append(" |||||||||| ");
            }

            System.out.println(sb.toString());
        }catch (Exception ex){
            throw ex;
        }
    }



    @Test
    public void AllPredicates(){
        SimpleSparqlRunner queryExecutioner = new SimpleSparqlRunner("http://localhost:3030/myGraph/query");


        ParameterizedSparqlString paraPathQuery =
                new ParameterizedSparqlString("SELECT  DISTINCT(?p)\n" +
                        "WHERE { ?s ?p ?o }\n" +
                        "LIMIT 100");

        try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
            ResultSet result = qe.execSelect();

            StringBuilder sb = new StringBuilder();

            while (result.hasNext()) {
                QuerySolution qs = result.next();

                sb.append(" predicate is : "+qs.get("?p"));
                sb.append(" |||||||||| ");
            }

            System.out.println(sb.toString());
        }catch (Exception ex){
            throw ex;
        }
    }

    @Test
    public void SelectDBpediaLinkFromYagoProperty(){
        SimpleSparqlRunner queryExecutioner = new SimpleSparqlRunner("http://localhost:3030/myGraph/query");


        ParameterizedSparqlString paraPathQuery =
                new ParameterizedSparqlString("SELECT ?p \n" +
                        "WHERE { <http://yago-knowledge.org/resource/actedIn> ?p ?o }\n" +
                        "LIMIT 100");

        try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
            ResultSet result = qe.execSelect();

            StringBuilder sb = new StringBuilder();

            while (result.hasNext()) {
                QuerySolution qs = result.next();


                sb.append(" predicate is : "+qs.get("?p"));

                sb.append(" -0-0-0- ");
            }

            System.out.println(sb.toString());
        }catch (Exception ex){
            throw ex;
        }
    }

}
