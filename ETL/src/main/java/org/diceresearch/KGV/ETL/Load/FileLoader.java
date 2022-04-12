package org.diceresearch.KGV.ETL.Load;

import org.diceresearch.KGV.ETL.DiskManager.HDDManager;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileLoader implements ILoad <List<SimpleRDF>,String > {
    @Override
    public void Save(List<SimpleRDF> forSave, String filename) throws IOException, ClassNotFoundException {
        HDDManager m = new HDDManager();
        String ss = m.giveFileLocation(filename);
        try(FileWriter fw = new FileWriter(ss, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for(SimpleRDF rdf: forSave){
                out.println(rdf.toString());
            }
        } catch (IOException e) {
            throw e;
        }
    }
}
