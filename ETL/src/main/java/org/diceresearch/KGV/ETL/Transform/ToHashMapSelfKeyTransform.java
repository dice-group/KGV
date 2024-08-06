package org.diceresearch.KGV.ETL.Transform;

import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Model.SimpleRDFWithVeracity;

import java.util.HashMap;
import java.util.List;

// here the key is veracity
public class ToHashMapSelfKeyTransform implements ITransform<HashMap<String,Double>, List<SimpleRDFWithVeracity>>{
    @Override
    public HashMap<String, Double> transform(List<SimpleRDFWithVeracity> input, String splitter) throws Exception {
        HashMap<String, Double> returnVal = new HashMap<>();

        for(SimpleRDFWithVeracity rdf : input){
            String key = getKey(rdf);
            if(returnVal.containsKey(key)){
             Double savedScore = returnVal.get(key);
             if(savedScore< rdf.getVeracity()){
                 returnVal.put(key, rdf.getVeracity());
             }
            }else {
                returnVal.put(key, rdf.getVeracity());
            }
        }

        return returnVal;
    }

    public String getKey(SimpleRDFWithVeracity rdf){
        return rdf.getSubject()+"\t"+rdf.getPredicate().toString()+"\t"+rdf.getObject()+"\t";
    }

    public String getKey(SimpleRDF rdf){
        return rdf.getSubject()+"\t"+rdf.getPredicate().toString()+"\t"+rdf.getObject()+"\t";
    }

}
