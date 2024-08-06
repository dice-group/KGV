package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Transform.NominalTransform;

import java.util.List;

// get an arff file
// convert string to nominale form
public class NominaleArffFile {

    static String arffFile = "/home/farshad/experiments/Trainfactchek/Fact/finaltraining/train1NegativeAndPositiv.arff";
    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws Exception{
        NominalTransform transformer = new NominalTransform();
        List<String> transformed = transformer.transform(arffFile, "");
    }
}
