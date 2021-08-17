package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.IExtractor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class calculateNumberOfEachPredicateIn1000File {
    public static Map<String,Integer> counter = new HashMap<>();
    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws IOException, ClassNotFoundException {
        IExtractor extractor = new FileExtractor();

        File rdf4Check = (File) extractor.Extract("Co" +
                "nverted1000FileYago3.txt");

        Scanner scanner = new Scanner(rdf4Check);
        String line = "";
        String predicate ="";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] splited = line.split(" ");
            if(counter.containsKey(splited[1])){
                Integer i = counter.get(splited[1]);
                counter.put(splited[1],i+1);
            }else{
                counter.put(splited[1],1);
            }
        }

        scanner.close();

        for(Map.Entry<String, Integer> entry : counter.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
    }
}
