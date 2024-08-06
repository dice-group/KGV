package org.diceresearch.KGV.ETL.Model;

import java.util.Objects;

public class SimpleRDFWithVeracity {
    private String Id;
    private String Subject;
    private String Predicate;
    private String Object;
    private Double veracity;

    public SimpleRDFWithVeracity(String id, String subject, String predicate, String object, Double veracity) {
        Id = id;
        Subject = subject;
        Predicate = predicate;
        Object = object;
        this.veracity = veracity;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getPredicate() {
        return Predicate;
    }

    public void setPredicate(String predicate) {
        Predicate = predicate;
    }

    public String getObject() {
        return Object;
    }

    public void setObject(String object) {
        Object = object;
    }

    public Double getVeracity() {
        return veracity;
    }

    public void setVeracity(Double veracity) {
        this.veracity = veracity;
    }

    public boolean sameTriple(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleRDFWithVeracity simpleRDF = (SimpleRDFWithVeracity) o;
        return  Objects.equals(getSubject(), simpleRDF.getSubject()) &&
                Objects.equals(getPredicate(), simpleRDF.getPredicate()) &&
                Objects.equals(getObject(), simpleRDF.getObject());
    }


}
