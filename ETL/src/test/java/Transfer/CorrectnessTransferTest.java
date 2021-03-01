package Transfer;

import com.sun.xml.internal.bind.v2.TODO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@SpringBootApplication
public class CorrectnessTransferTest {
    @Test
    public void CastBoolean_ShouldWorkFine_whileInputIsValid() throws Exception {
        CorrectnessTransfer transfer = new CorrectnessTransfer();

        Boolean actual = transfer.CastBoolean("0");
        Assertions.assertEquals(actual,false);

        actual = transfer.CastBoolean("1");
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
    public void Transfer_ShouldWork_withValidFile() throws Exception {
        ITransfer<HashMap<String, Boolean>, File> transfer = new CorrectnessTransfer();
        File resource = new ClassPathResource("data/TransferCorrectnessTestFile.txt").getFile();
        HashMap<String, Boolean> actual = transfer.Transfer(resource,"\t");
        Assertions.assertEquals(actual.get("201179"),true);
        Assertions.assertEquals(actual.get("201138"),false);
        Assertions.assertEquals(actual.get("201137"),true);
    }
}
