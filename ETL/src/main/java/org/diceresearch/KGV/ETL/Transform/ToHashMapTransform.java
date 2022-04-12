package org.diceresearch.KGV.ETL.Transform;

import org.diceresearch.KGV.ETL.Model.SimpleRDF;

import java.io.File;
import java.util.*;

public class ToHashMapTransform implements ITransform<HashMap<String,String>, List<SimpleRDF>>{
    @Override
    public HashMap<String, String> Transform(List<SimpleRDF> input, String splitter) throws Exception {
        HashMap<String, String> returnVal = new HashMap<>();

        for(SimpleRDF rdf : input){
            returnVal.put(rdf.getSubject(), rdf.getObject());
        }

        return returnVal;
    }
}
