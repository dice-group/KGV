package org.diceresearch.KGV.ETL.Transform;

import org.diceresearch.KGV.ETL.Model.SimpleLabeledRDF;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class BeliefTransform implements ITransform<List<SimpleRDF>, File>  {
    @Override
    public List<SimpleRDF> Transform(File input, String splitter) throws Exception {

        List<SimpleRDF> returnValue = new ArrayList<>();

        Scanner scanner = new Scanner(input);

        String line ="";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] words = line.split(splitter);
            if(words.length<4){
                throw new Exception("Could not parse this line:"+line);
            }
            if(words.length>4){
                String w="";
                for(int i = 0 ; i <= words.length - 4 ; i++){
                    if(i+1<=words.length - 4) {
                        w += words[3 + i] + "\t";
                    }else{
                        w += words[3 + i];
                    }
                }
                words[3] = w;
            }
            SimpleRDF statement = new SimpleRDF(words[0],words[1],words[2],words[3]);
            returnValue.add(statement);
        }
        scanner.close();
        return returnValue;
    }

    public List<SimpleLabeledRDF> Merge(HashMap<String, Boolean> correctness, List<SimpleRDF> input){
        List<SimpleLabeledRDF> returnValue = new ArrayList<>();

        for (SimpleRDF s:input) {
            Boolean c = correctness.get(s.getId());
            returnValue.add(new SimpleLabeledRDF(s.getId(),s,c));
        }

        return  returnValue;
    }
}
