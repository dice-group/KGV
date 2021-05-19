package org.diceresearch.KGV.ETL.Load;

import org.apache.jena.rdf.model.Model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Model2ttlLoader implements ILoad<Model,String> {

    @Override
    public void Save(Model forSave, String destination) throws IOException, ClassNotFoundException {
        // Write as Turtle via model.write
        try (OutputStream out = new FileOutputStream(destination)) {
            forSave.write(out, "TTL") ;
        }
    }
}
