package Extract;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileExtractor implements IExtractor<File, String> {
    @Override
    public File Extract(String location) throws IOException, ClassNotFoundException {
        Class cls=this.getClass();
       // Class cls = Class.forName("org.dice-research.KGV.ETL.FileExtractor");

        // returns the ClassLoader object associated with this Class
        ClassLoader cLoader = cls.getClassLoader();

        // input stream
        InputStream inputStream = cLoader.getResourceAsStream(location);

        File targetFile = stream2file(inputStream);

        return targetFile;
    }

    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";

    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}
