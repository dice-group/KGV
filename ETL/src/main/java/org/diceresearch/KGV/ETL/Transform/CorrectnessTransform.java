package org.diceresearch.KGV.ETL.Transform;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class CorrectnessTransform implements ITransform<HashMap<String, Boolean>, File> {
    @Override
    public HashMap<String, Boolean> Transform(File input,String splitter) throws Exception {

        HashMap<String, Boolean> returnValue = new HashMap<>();
        Scanner scanner = new Scanner(input);
        String id = "";
        String line = "";
        Boolean correctness = false;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            id = line.split(splitter)[0];
            // 0 is false
            // 1 is true
            correctness = CastBoolean(line.split(splitter)[1]);
            returnValue.put(id, correctness);
        }
        scanner.close();
        return returnValue;
    }

    public Boolean CastBoolean(String s) throws Exception {
        if (s.equals("0")) return false;
        if (s.equals("1")) return true;
        throw new Exception("Unknown character for cast to boolean :" + s);
    }
}
