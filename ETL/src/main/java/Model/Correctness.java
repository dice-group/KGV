package Model;

import java.util.Objects;

public class Correctness {
    private String Id;
    private Boolean correctness;

    public Correctness(){}

    public Correctness(String id, Boolean correctness) {
        Id = id;
        this.correctness = correctness;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Boolean getCorrectness() {
        return correctness;
    }

    public void setCorrectness(Boolean correctness) {
        this.correctness = correctness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Correctness that = (Correctness) o;
        return Objects.equals(Id, that.Id) &&
                Objects.equals(correctness, that.correctness);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, correctness);
    }
}
