package org.diceresearch.KGV.ETL.Transform;

import java.util.*;
import java.io.File;
import java.util.Scanner;

public class TabSeperetadResultToMapTransform implements ITransform<Map<String,Double>,File> {
    @Override
    public Map<String, Double>  Transform(File input , String splitter) throws Exception {
        Map<String, Double> returnVal = new HashMap<>();

        String line ="";

        Scanner scanner = new Scanner(input);
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] words = line.split(splitter);

            returnVal.put(words[0]+"\t"+words[1]+"\t"+words[2],Double.parseDouble(words[3]));
        }
        return  returnVal;
    }
}
