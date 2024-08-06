package org.diceresearch.KGV.ETL.Transform;

import org.diceresearch.KGV.ETL.Model.SimpleRDFWithVeracity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class SimpleRdfSmartIdWithVeracityTransform implements ITransform <List<SimpleRDFWithVeracity>, File>{
    @Override
    public List<SimpleRDFWithVeracity> transform(File input, String splitter) throws Exception {
        Scanner scanner = new Scanner(input);
        String line;
        List<SimpleRDFWithVeracity> retval = new ArrayList<>();
        long progress = 0;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            line = line.replace("<","").replace(">","");
            String[] parts = line.split(splitter);
            if(parts.length==4){
                // generate random ID
                SimpleRDFWithVeracity temp = new SimpleRDFWithVeracity( UUID.randomUUID().toString(), parts[0],parts[1],parts[2],Double.parseDouble(parts[3]));
                retval.add(temp);
            } else {
                if(parts.length==5){
                    // use exist ID
                    SimpleRDFWithVeracity temp = new SimpleRDFWithVeracity( parts[4], parts[0],parts[1],parts[2],Double.parseDouble(parts[3]));
                    retval.add(temp);
                }else{
                    // invalid
                    throw new Exception("this line is not valid " + line);
                }
            }
            progress = progress+1;
            if(progress%100==0){System.out.println(progress);}
        }
        scanner.close();
        return retval;
    }
}
