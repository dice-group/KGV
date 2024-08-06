package org.diceresearch.KGV.ETL.Transform;

import weka.core.*;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.*;

public class NominalTransform implements ITransform<List<String>, String> {

    int dataSectionLineNumber = 18;
    List<Integer> removeAttributes = new ArrayList<Integer>();
    Set<String> filenameSet = new HashSet<>(); //25
    Set<String> subjectSet = new HashSet<>();// 26
    Set<String> phraseSet = new HashSet<>();// 27
    Set<String> objectSet = new HashSet<>();//28
    Set<String> contextSet = new HashSet<>();//29


    @Override
    public List<String> transform(String filePath, String splitter) throws Exception {

        Instances instances = new Instances(new BufferedReader(new FileReader(filePath)));

        for(int i = 0 ; i <instances.numInstances() ; i++){
            Instance instance = instances.instance(i);
            //filenameSet.add(instance.stringValue(25));
            //subjectSet.add(instance.stringValue(26));
            //phraseSet.add(instance.stringValue(27).toString());*/
            //objectSet.add(instance.stringValue(28).toString());
            contextSet.add(instance.stringValue(29).toString());
        }

        Print(contextSet);

        return null;
    }

    private void Print(Set<String> filenameSet) {
        System.out.print("{");
        for(String s : filenameSet){
            System.out.print(s+", ");
        }
        System.out.print("}");
    }
}
