package org.diceresearch.KGV.ETL.Transform;

import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Model.SimpleRDFWithVeracity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CopaalOutPutTransform implements ITransform<List<SimpleRDF>, File>  {

    private double threshold = 0.0;

    public CopaalOutPutTransform(){
        threshold = 0.0;
    }

    public void SetCopaalOutPutTransform(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public List<SimpleRDF> transform(File input, String splitter) throws Exception {

        List<SimpleRDF> returnValue = new ArrayList<>();

        Scanner scanner = new Scanner(input);

        String line ="";
        int i = -1000;

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] words = line.split(splitter);
            if(words.length!=4 && words.length!=5){
                throw new Exception("Could not parse this line:"+line);
            }
            if(words.length==4) {
                SimpleRDF statement = new SimpleRDF(String.valueOf(i), words[0], words[1], words[2]);
                double score = 0;
                if(!words[3].equals("null")) {
                     score = Double.parseDouble(words[3]);
                }
                if (score <= threshold) {
                    statement.setCorrectness(false);
                } else {
                    statement.setCorrectness(true);
                }
                returnValue.add(statement);
            }

            if(words.length==5) {
                SimpleRDF statement = new SimpleRDF(words[4], words[0], words[1], words[2]);
                double score = Double.parseDouble(words[3]);
                if (score <= threshold) {
                    statement.setCorrectness(false);
                } else {
                    statement.setCorrectness(true);
                }
                returnValue.add(statement);
            }

        }
        scanner.close();
        return returnValue;
    }
    public List<SimpleRDFWithVeracity> TransformWithVeracity(File input, String splitter) throws Exception {

        List<SimpleRDFWithVeracity> returnValue = new ArrayList<>();

        Scanner scanner = new Scanner(input);

        String line ="";
        int i = -1000;

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] words = line.split(splitter);
            if(words.length!=4 && words.length!=5){
                throw new Exception("Could not parse this line:"+line);
            }
            if(words.length==4) {
                SimpleRDFWithVeracity statement = new SimpleRDFWithVeracity(String.valueOf(i), words[0], words[1], words[2],Double.parseDouble(words[3]));
                returnValue.add(statement);
            }

            if(words.length==5) {
                SimpleRDFWithVeracity statement = new SimpleRDFWithVeracity(words[4], words[0], words[1], words[2],Double.parseDouble(words[3]));
                returnValue.add(statement);
            }

        }
        scanner.close();
        return returnValue;
    }
}
