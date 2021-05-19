package org.diceresearch.KGV.ETL.Extract;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileExtractorOutOfPackage implements IExtractor<File, String> {
    @Override
    public File Extract(String location) throws IOException, ClassNotFoundException {
        File f = new File(location);

        if(!f.exists()){
            throw new IOException("File not find");
        }

        return f;
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