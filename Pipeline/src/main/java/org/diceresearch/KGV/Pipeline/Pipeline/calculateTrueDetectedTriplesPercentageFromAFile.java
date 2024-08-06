package org.diceresearch.KGV.Pipeline.Pipeline;

import org.apache.jena.base.Sys;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Model.SimpleRDFWithVeracity;
import org.diceresearch.KGV.ETL.Transform.CopaalOutPutTransform;

import java.io.File;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;
import java.util.List;

public class calculateTrueDetectedTriplesPercentageFromAFile {

    static Double threshold = 0.1;
    private static String fileToProcess ="/home/farshad/experiments/yago2Copaal/subsetResultRegenarated/ver3/finalfactcheck.txt";
    //private static String fileToProcess ="/home/farshad/experiments/yago2Copaal/Results/Subset/0shortResults_yagoSubset2hasChild.txt";

    public static void main(String[] args) throws Exception {
        Run();
    }

    static Hashtable<String,Integer> allNumbers = new Hashtable<>();
    static Hashtable<String,Integer> positivNumbers = new Hashtable<>();

    public static void Run() throws Exception {
        IExtractor extractor = new FileExtractorOutOfPackage();

        File outPutOfCopaal = (File) extractor.Extract(fileToProcess);
        CopaalOutPutTransform transformer = new CopaalOutPutTransform();
        transformer.SetCopaalOutPutTransform(0.1);
        List<SimpleRDFWithVeracity> forCheck = transformer.TransformWithVeracity(outPutOfCopaal, "\t");
        int trueFacts = 0;
        for(SimpleRDFWithVeracity rdf: forCheck){
            String key = rdf.getPredicate().toLowerCase().replaceAll("[^\\p{Print}]", "").trim();

            if (allNumbers.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(key))) {
                // +1
                int c = allNumbers.get(key);
                allNumbers.put(key,c+1);
            } else {
                // insert 1
                allNumbers.put(key,1);
            }


            if (positivNumbers.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(key))) {
                // +1
                Integer c = positivNumbers.get(key);
                if(rdf.getVeracity()>threshold) {
                    c = c + 1;
                }
                positivNumbers.put(key,c);
            } else {
                // insert 1
                // insert 1
                if(rdf.getVeracity()>threshold) {
                    positivNumbers.put(key,1);
                }
            }
        }


        System.out.println(allNumbers);

        // get the set of keys from the hashtable
        Set<String> keySet = allNumbers.keySet();

        // create an iterator for the keys
        Iterator<String> iterator = keySet.iterator();

        // iterate over the keys and print the key-value pairs
        while (iterator.hasNext()) {
            String key = iterator.next();
            int value = allNumbers.get(key);
            System.out.println(key + ": " + value);
        }

        System.out.println("-------------");

        keySet = positivNumbers.keySet();
        iterator = keySet.iterator();

        // iterate over the keys and print the key-value pairs
        while (iterator.hasNext()) {
            String key = iterator.next();
            int value = positivNumbers.get(key);
            System.out.println(key + ": " + value);
        }

    }
}
