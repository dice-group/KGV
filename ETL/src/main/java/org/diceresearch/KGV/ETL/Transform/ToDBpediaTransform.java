package org.diceresearch.KGV.ETL.Transform;

import org.apache.commons.text.StringEscapeUtils;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.QueryRunner.QueryRunner.HttpRequestRunner;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToDBpediaTransform implements ITransform<List<SimpleRDF>,List<SimpleRDF>>{

    private HashMap<String,String> Yago2dbpediaPredicates;

    public ToDBpediaTransform(HashMap<String, String> yago2dbpediaPredicates) {
        Yago2dbpediaPredicates = yago2dbpediaPredicates;
    }

    @Override
    public List<SimpleRDF> Transform(List<SimpleRDF> input, String splitter) throws Exception {
        List<SimpleRDF> retVal = new ArrayList<>();
        for(SimpleRDF rdf : input){
            System.out.println(rdf);
            if(!IsValidPredicate(rdf.getPredicate())){
                System.out.println("The predicate is not valid : "+ rdf.getPredicate());
                continue;
            }
            SimpleRDF convertedRdf = ConvertToDBpedia(rdf);
            if(
                    IsValidInDBpedia(convertedRdf.getSubject())&&IsValidInDBpedia(convertedRdf.getObject())
            ){
                retVal.add(convertedRdf);
            }

            System.out.println("------------------------------------");
        }
        return retVal;
    }

    private boolean IsValidInDBpedia(String subject) throws Exception {
       String url = subject.replace("<","").replace(">","");
        //url = StringEscapeUtils.unescapeJava(url);
        System.out.println(url);

        String lastPart = extractLastPartOfURI(url);
        lastPart = URLEncoder.encode(lastPart, "UTF-8");
        System.out.println(lastPart);

        String firstPart = excludeLastPartOfURI(url);
        System.out.println(firstPart);


        url = firstPart+lastPart;
        System.out.println(url);
        return true;
        /*if(HttpRequestRunner.isValidDBpediaURL(url)){
            return true;
        }else {
            return false;
        }*/
    }

    private SimpleRDF ConvertToDBpedia(SimpleRDF input) {
        String DBpediaPrefix = "<https://dbpedia.org/resource/";

        String subject = input.getSubject();
        String object = input.getObject();

        if(subject.contains("yago-knowledge")){
            subject = DBpediaPrefix+StringEscapeUtils.unescapeJava(extractLastPartOfURI(input.getSubject()));
        }

        if(object.contains("yago-knowledge")){
            object = DBpediaPrefix+StringEscapeUtils.unescapeJava(extractLastPartOfURI(input.getObject()));
        }

        SimpleRDF retVal = new SimpleRDF(input.getId(), subject, Yago2dbpediaPredicates.get(input.getPredicate()), object);

        return  retVal;
    }

    private String extractLastPartOfURI(String input){
        String[] parts = input.split("/");
        return parts[parts.length-1];
    }

    private String excludeLastPartOfURI(String input){
        String[] parts = input.split("/");
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < parts.length-1 ; i++){
            sb.append(parts[i]+"/");
        }
        return sb.toString();
    }

    private boolean IsValidPredicate(String predicate) {
        if(!Yago2dbpediaPredicates.containsKey(predicate)){
            return false;
        }else{
            if(Yago2dbpediaPredicates.get(predicate).equals("<>")){
                return false;
            }
        }

        return true;
    }
}
