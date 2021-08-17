package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractor;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;

import java.io.File;
import java.util.Scanner;
// read the facade result file and count the number of true facts for copaal fact check and facade
public class CalculateResultFromFilePipeline {
    static int pathLen = 3;
    static String predicate = "location";
    static boolean isVirtual = true;

    static String adr ="/home/farshad/repos/KGV/ResultsFacade";

    static String sss = predicate+"_VT_"+isVirtual+"_pl_"+pathLen;
    static String FileNameValid = adr+"/resultIsValid_"+sss+"_facade.txt";


    public static void main(String[] args) throws Exception {
        Run();
    }
    public static void Run() throws Exception {
        IExtractor extractor = new FileExtractorOutOfPackage();

        File resultFile = (File) extractor.Extract(FileNameValid);

        Scanner scanner = new Scanner(resultFile);

        String line ="";

        int copalC = 0;
        int factC = 0;
        int facadeC = 0;
        int lineC = 0;

        while (scanner.hasNextLine()) {
            lineC++;
            line = scanner.nextLine();
            String[] words = line.split("\t");
            double copalScore = Double.parseDouble(words[3]);
            double factcheckScore = Double.parseDouble(words[4]);
            double facadeScore = Double.parseDouble(words[5]);

            if(copalScore>0)copalC++;
            if(factcheckScore>0)factC++;
            if(facadeScore>0)facadeC++;



        }

        System.out.println(lineC+" "+copalC+" "+factC+" "+facadeC+" ");
    }
}
