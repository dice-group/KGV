package org.diceresearch.KGV.ETL.Transform;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ToSetTransform implements ITransform<Set<String>, File>{
    @Override
    public Set<String> Transform(File input, String splitter) throws Exception {
        Set<String> returnVal = new HashSet<>();

        Scanner scanner = new Scanner(input);
        String line = "";
        String predicate ="";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            predicate = line.split(splitter)[1];
            returnVal.add(predicate);
        }
        scanner.close();

        return  returnVal;
    }
}
