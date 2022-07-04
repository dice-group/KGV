package org.diceresearch.KGV.ETL.Transform;

import org.diceresearch.KGV.ETL.Model.SimpleRDF;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class SimpleRdfRandomIdTransform implements ITransform <List<SimpleRDF>, File>{
    @Override
    public List<SimpleRDF> Transform(File input, String splitter) throws Exception {
        Scanner scanner = new Scanner(input);
        String line;
        List<SimpleRDF> retval = new ArrayList<>();
        long progress = 0;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            line = line.replace("<","").replace(".","").replace(">","");
            String[] parts = line.split(splitter);
            SimpleRDF temp = new SimpleRDF( UUID.randomUUID().toString(), parts[0],parts[1],parts[2]);
            retval.add(temp);
            progress = progress+1;
            if(progress%100==0){System.out.println(progress);}
        }
        scanner.close();
        return retval;
    }
}
