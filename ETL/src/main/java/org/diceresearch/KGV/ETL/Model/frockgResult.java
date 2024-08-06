package org.diceresearch.KGV.ETL.Model;

import java.util.Objects;

public class frockgResult {

    private String Id;
    private String Subject;
    private String Predicate;
    private String Object;
    private double copaalVeracity;
    private double factcheckVeracity;
    private double frockgVeracity;
    private boolean isTripleCorrect;

    public frockgResult(String id, String subject, String predicate, String object, double copaalVeracity, double factcheckVeracity, double frockgVeracity, boolean isTripleCorrect) {
        Id = id;
        Subject = subject;
        Predicate = predicate;
        Object = object;
        this.copaalVeracity = copaalVeracity;
        this.factcheckVeracity = factcheckVeracity;
        this.frockgVeracity = frockgVeracity;
        this.isTripleCorrect = isTripleCorrect;
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

    public double getCopaalVeracity() {
        return copaalVeracity;
    }

    public void setCopaalVeracity(double copaalVeracity) {
        this.copaalVeracity = copaalVeracity;
    }

    public double getFactcheckVeracity() {
        return factcheckVeracity;
    }

    public void setFactcheckVeracity(double factcheckVeracity) {
        this.factcheckVeracity = factcheckVeracity;
    }

    public double getFrockgVeracity() {
        return frockgVeracity;
    }

    public void setFrockgVeracity(double frockgVeracity) {
        this.frockgVeracity = frockgVeracity;
    }

    public boolean isTripleCorrect() {
        return isTripleCorrect;
    }

    public void setTripleCorrect(boolean tripleCorrect) {
        isTripleCorrect = tripleCorrect;
    }

    public boolean sameTriple(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        frockgResult simpleRDF = (frockgResult) o;
        return  Objects.equals(getSubject(), simpleRDF.getSubject()) &&
                Objects.equals(getPredicate(), simpleRDF.getPredicate()) &&
                Objects.equals(getObject(), simpleRDF.getObject());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("frockgResult{");
        sb.append("Id='").append(Id).append('\'');
        sb.append(", Subject='").append(Subject).append('\'');
        sb.append(", Predicate='").append(Predicate).append('\'');
        sb.append(", Object='").append(Object).append('\'');
        sb.append(", copaalVeracity=").append(copaalVeracity);
        sb.append(", factcheckVeracity=").append(factcheckVeracity);
        sb.append(", frockgVeracity=").append(frockgVeracity);
        sb.append(", isTripleCorrect=").append(isTripleCorrect);
        sb.append('}');
        return sb.toString();
    }
}
