package Extract;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

@SpringBootApplication
public class FileExtractorTest {

    @Test
    public void FileExtractor_ShouldReadFile() throws IOException, ClassNotFoundException {
        IExtractor<File, String> extractor = new FileExtractor();
        File actual = extractor.Extract("FileExtractorTestFile.txt");

        Scanner myReader = new Scanner(actual);
        String actualFileText = "";
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            actualFileText += data;
        }

        String expected = "THIS IS TEST FILE.";

        Assertions.assertEquals(actualFileText, expected);
    }
}
