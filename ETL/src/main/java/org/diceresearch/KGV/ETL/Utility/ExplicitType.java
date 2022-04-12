package org.diceresearch.KGV.ETL.Utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class ExplicitType {
    /**
     * For a given statement, this method searches for the predicate of a model
     * inside the Ontology. If found in the Ontology, it then extracts the domain
     * and range. Creating and adding a new triple with the inferred type to the
     * model.
     *
     * @param statement statement in which we want to check the predicate in the
     *                  ontology
     * @param ontModel  the ontology model
     * @param newModel  where we add the new triples and therefore, where we check
     *                  if the statement is already existing in the model or not
     * @return a set of statements inferred from one property
     */
    public Set<Statement> searchType(Statement statement, OntModel ontModel, Model newModel) {
        Set<Statement> newStmts = new HashSet<>();
        Resource subject = statement.getSubject();
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject();

        // search for the predicate of the model in the ontology
        OntProperty property = ontModel.getOntProperty(predicate.toString());
        if (property != null) {
            List<? extends OntResource> domain = property.listDomain().toList();
            for (OntResource curResource : domain) {
                Statement subjType = ResourceFactory.createStatement(subject, RDF.type, curResource);
                if (!newModel.contains(subjType)) {
                    newStmts.add(subjType);
                }
            }
            if (object.isResource()) {
                List<? extends OntResource> range = property.listRange().toList();
                for (OntResource curResource : range) {
                    Statement objType = ResourceFactory.createStatement(object.asResource(), RDF.type, curResource);
                    if (!newModel.contains(objType)) {
                        newStmts.add(objType);
                    }
                }
            }
        }
        return newStmts;
    }
}
