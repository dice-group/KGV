package Model;

public class SimpleRDF {
    private String Id;
    private String Subject;
    private String Predicate;
    private String Object;
    private Boolean correctness;

    public SimpleRDF(){}

    public SimpleRDF(String id, String sobject, String predicate, String object) {
        Id = id;
        Subject = sobject;
        Predicate = predicate;
        Object = object;
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

    public void setSubject(String sobject) {
        Subject = sobject;
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
}
