package org.diceresearch.KGV.ETL.Transform;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.HashMap;

@SpringBootApplication
public class CorrectnessTransformTest {
    @Test
    public void CastBoolean_ShouldWorkFine_whileInputIsValid() throws Exception {
        CorrectnessTransform transform = new CorrectnessTransform();

        Boolean actual = transform.CastBoolean("0");
        Assertions.assertEquals(actual,false);

        actual = transform.CastBoolean("1");
        Assertions.assertEquals(actual,true);

    }

    @Test
    public void CastBoolean_ShouldThrowExp_whileInputIsNotValid() throws Exception {
        // TODO
/*        CorrectnessTransfer transfer = new CorrectnessTransfer();

        Boolean actual = transfer.CastBoolean(" ");

        Exception exception = assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt("1a");
        });
        assertTrue(actualMessage.contains(expectedMessage));*/

    }

            /*201179	1
            201138	0
            201137	1*/

    @Test
    public void Transform_ShouldWork_withValidFile() throws Exception {
        ITransform<HashMap<String, Boolean>, File> transform = new CorrectnessTransform();
        File resource = new ClassPathResource("data/TransferCorrectnessTestFile.txt").getFile();
        HashMap<String, Boolean> actual = transform.Transform(resource,"\t");
        Assertions.assertEquals(actual.get("201179"),true);
        Assertions.assertEquals(actual.get("201138"),false);
        Assertions.assertEquals(actual.get("201137"),true);
    }
}
