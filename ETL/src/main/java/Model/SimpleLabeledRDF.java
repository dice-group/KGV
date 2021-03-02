package Model;

public class SimpleLabeledRDF {
    private String Id;
    SimpleRDF simpleRDF;
    private Boolean correctness;

    public SimpleLabeledRDF() {}

    public SimpleLabeledRDF(String id, SimpleRDF simpleRDF, Boolean correctness) {
        Id = id;
        this.simpleRDF = simpleRDF;
        this.correctness = correctness;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public SimpleRDF getSimpleRDF() {
        return simpleRDF;
    }

    public void setSimpleRDF(SimpleRDF simpleRDF) {
        this.simpleRDF = simpleRDF;
    }

    public Boolean getCorrectness() {
        return correctness;
    }

    public void setCorrectness(Boolean correctness) {
        this.correctness = correctness;
    }
}
