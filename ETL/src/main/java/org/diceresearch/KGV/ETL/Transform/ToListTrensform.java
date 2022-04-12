package org.diceresearch.KGV.ETL.Transform;

import java.io.File;
import java.util.*;

public class ToListTrensform implements ITransform<List<List<String>>, File>{
    @Override
    public List<List<String>> Transform(File input, String splitter) throws Exception {
        List<List<String>> returnVal = new ArrayList<>();

        Scanner scanner = new Scanner(input);
        String line = "";
        String predicate ="";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            returnVal.add(Arrays.asList(line.split(splitter)));
        }

        scanner.close();

        return  returnVal;
    }
}
