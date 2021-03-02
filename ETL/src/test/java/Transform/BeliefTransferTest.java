package Transform;

import Model.SimpleLabeledRDF;
import Model.SimpleRDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class BeliefTransferTest {

    @Test
    public void Transfer_ShouldWork_WithValidFile() throws Exception {
        BeliefTransform transform = new BeliefTransform();
        File resource = new ClassPathResource("data/TransformBeliefYagoTestFile.txt").getFile();
        List<SimpleRDF> actual = transform.Transform(resource,"\t");

        Assertions.assertEquals(actual.size(),3);

        Assertions.assertEquals(actual.get(0).getId(),"1");
        Assertions.assertEquals(actual.get(0).getSubject(),"Azerbaijan");
        Assertions.assertEquals(actual.get(0).getPredicate(),"hasOfficialLanguage");
        Assertions.assertEquals(actual.get(0).getObject(),"Azerbaijani_language");

        Assertions.assertEquals(actual.get(1).getId(),"2");
        Assertions.assertEquals(actual.get(1).getSubject(),"Augustus");
        Assertions.assertEquals(actual.get(1).getPredicate(),"hasChild");
        Assertions.assertEquals(actual.get(1).getObject(),"Julia_the_Elder");

        Assertions.assertEquals(actual.get(2).getId(),"3");
        Assertions.assertEquals(actual.get(2).getSubject(),"Arthur_Aikin");
        Assertions.assertEquals(actual.get(2).getPredicate(),"isCitizenOf");
        Assertions.assertEquals(actual.get(2).getObject(),"England");
    }

    @Test
    public void Merge_ShouldMergeWell(){
        BeliefTransform transform = new BeliefTransform();

        HashMap<String, Boolean> correctness = new HashMap<>();
        correctness.put("1",true);
        correctness.put("2",false);

        List<SimpleRDF> input = new ArrayList<>();
        input.add(new SimpleRDF("1","a","a","a"));
        input.add(new SimpleRDF("2","b","b","b"));

        List<SimpleLabeledRDF> actual = transform.Merge(correctness,input);

        Assertions.assertEquals(actual.size(),2);

        Assertions.assertEquals(actual.get(0).getId(),"1");
        Assertions.assertEquals(actual.get(0).getCorrectness(),true);
        Assertions.assertEquals(actual.get(0).getSimpleRDF().getObject(),"a");

        Assertions.assertEquals(actual.get(1).getId(),"2");
        Assertions.assertEquals(actual.get(1).getCorrectness(),false);
        Assertions.assertEquals(actual.get(1).getSimpleRDF().getObject(),"b");

    }
}
