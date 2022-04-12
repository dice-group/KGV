package org.diceresearch.KGV.Pipeline.Pipeline;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.jena.base.Sys;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDF;
import org.diceresearch.KGV.ETL.Transform.ITransform;
import org.diceresearch.KGV.ETL.Transform.ToListTrensform;
import org.yaml.snakeyaml.events.Event;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** read a tsv file and interpret the results */
public class InterpretSumerizersResultPipeline {

    static Integer lastIdGroundTruthMap = 1;
    static String folderpath = "/home/farshad/repos/CalculatedResults/";
    static Map<SimpleRDF, Integer> groundTruthMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        run();
    }
    public static void run() throws Exception {
        IExtractor extractor = new FileExtractorOutOfPackage();
        ITransform<List<List<String>>, File> transformer = new ToListTrensform();

        File file = (File)extractor.Extract("/home/farshad/repos/CopaalConfigTestResults/results (7th copy).tsv");
        List<List<String>> toProcess = transformer.Transform(file,"\t");

        Map<String,TrueAndFalseCounter> map = new HashMap<>();
        // the columns
        //0 time  1 path length  2 virtual type  3 counter retriver name  4 pnmpi or npmi
        //5 summarist  6 correctness  7 subject  8 predicate  9 object  10 output

        System.out.println(toProcess.get(0).size());
        System.out.println(toProcess.size());

        // for each line
        for(List<String> line : toProcess){
            // the name of summerizer , our key for our map
            String sumName = line.get(5);

            // create an empty entity in map (empty means set counters 0 )
            map = makeEmptyEntityInMapIfNeed(sumName,map);

            // if there is an entity , we retrive it to update the values
            TrueAndFalseCounter entity = map.get(sumName);

            // add one to total fact
            entity.addAllFacts();

            // get the calculated veracity
            double veracity = extractTheVeracity(line.get(10));

            // update number true detected facts and false detected facts at all
            entity = calculateVeracityCounter(veracity,entity);

            // update number of True facts and False facts and TT,TF,FT,FF values
            // TT = fact is true and detected true
            // TF = fact is true and detected false
            // FT = fact is false and detected true
            // FF = fact is false and detected false
            entity = calculateTTFF(line.get(6).toLowerCase(),veracity,entity);

            //write the ground truth File
            int idForSummarizerFile = writeGroundTruthFile(line.get(6),line.get(7),line.get(8),line.get(9),folderpath);
            //write the summarizer file
            writeTheSummarizerFile(idForSummarizerFile,line.get(1),line.get(2),line.get(3),line.get(4),line.get(5),veracity,line.get(7),line.get(8),line.get(9),folderpath);
            // set in map
            map.put(sumName, entity);
        }

        //print result
        for (Map.Entry<String,TrueAndFalseCounter> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "\t==>" + entry.getValue().toString());
        }
    }

    //0 time  1 path length  2 virtual type  3 counter retriver name  4 pnmpi or npmi
    //5 summarist  6 correctness  7 subject  8 predicate  9 object  10 output

    private static void writeTheSummarizerFile(Integer idForSummarizerFile, String pathLength, String virtualType,
                                               String counterRetriverName, String pnmpiOrNpmi, String summarist,
                                               double veracity, String subject, String predicate,
                                               String object, String folderpath) {
        //folderpath = folderpath +pathLength+virtualType+counterRetriverName+pnmpiOrNpmi+summarist+".nt";
        folderpath = folderpath + summarist+".nt";
        writeResult(idForSummarizerFile.toString(), subject, predicate, object,veracity,folderpath);
    }

    private static int writeGroundTruthFile(String correctness, String subject, String predicate, String object, String folderpath) {
        // find id of the fact
        SimpleRDF tempRDF = new SimpleRDF("0",subject,predicate,object);
        if(!groundTruthMap.containsKey(tempRDF)){
            lastIdGroundTruthMap = lastIdGroundTruthMap +1;
            groundTruthMap.put(tempRDF,lastIdGroundTruthMap);
            //write in the file
            if(correctness.equals("true")) {
                writeResult(lastIdGroundTruthMap.toString(), subject, predicate, object,1.0,folderpath+"groundTruth.nt");
            }else{
                writeResult(lastIdGroundTruthMap.toString(), subject, predicate, object,0.0,folderpath+"groundTruth.nt");
            }
        }
        int id = groundTruthMap.get(tempRDF);
        return id;
    }

    public static void writeResult(String id, String subject, String predicate, String object, Double score,String outPutPath) {
        StringBuilder sb = new StringBuilder();

        id = "<http://swc2017.aksw.org/task2/dataset/"+id+">";

        sb.append(id);
        sb.append(" <http://swc2017.aksw.org/hasTruthValue> \""+score+"\"^^<http://www.w3.org/2001/XMLSchema#double> .");
        sb.append(System.lineSeparator());
        //line 2
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ");
        sb.append("<"+subject+"> .");
        sb.append(System.lineSeparator());

        //line 3
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> ");
        sb.append("<"+predicate+"> .");
        sb.append(System.lineSeparator());

        //line 4
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> ");
        sb.append("<"+object+"> .");
        sb.append(System.lineSeparator());

        //line 5
        sb.append(id);
        sb.append(" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .");

        try(FileWriter fw = new FileWriter(outPutPath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(sb.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static TrueAndFalseCounter calculateTTFF(String s, double veracity, TrueAndFalseCounter entity) {
        if(s.equals("true")){
            // the fact is true
            entity.addTrueFacts();
            if(veracity>0){
                entity.addTT();
            }else{
                entity.addTF();
            }
        }else{
            // the fact is false
            entity.addFalseFacts();
            if(veracity>0){
                entity.addFT();
            }else{
                entity.addFF();
            }
        }
        return entity;
    }

    private static TrueAndFalseCounter calculateVeracityCounter(double veracity, TrueAndFalseCounter entity) {
        if(veracity>0){
            entity.addTrueDetectedFacts();
        }else{
            entity.addFalseDetectedFacts();
        }
        return entity;
    }

    private static double extractTheVeracity(String jsonResult) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonResult);
        String score = json.get("veracityValue").toString();
        String s = score.substring(0, Math.min(score.length(), 6));
        double veracity = Double.parseDouble(s);
        return veracity;
    }

    private static Map<String, TrueAndFalseCounter> makeEmptyEntityInMapIfNeed(String s, Map<String, TrueAndFalseCounter> map) {
        // make an empty entity in map
        if(!map.containsKey(s)){
            map.put(s, new TrueAndFalseCounter());
        }
        return map;
    }

    static class TrueAndFalseCounter{
        private int allFacts;
        private int trueFacts;
        private int falseFacts;
        private int trueDetectedFacts;
        private int falseDetectedFacts;
        private int TT;
        private int TF;
        private int FF;
        private int FT;

        @Override
        public String toString() {
            return "TrueAndFalseCounter{" +
                    "allFacts=" + allFacts +
                    ", trueFacts=" + trueFacts +
                    ", falseFacts=" + falseFacts +
                    ", trueDetectedFacts=" + trueDetectedFacts +
                    ", falseDetectedFacts=" + falseDetectedFacts +
                    ", TT=" + TT +
                    ", TF=" + TF +
                    ", FF=" + FF +
                    ", FT=" + FT +
                    '}';
        }

        public TrueAndFalseCounter(){
            this.allFacts = 0;
            this.trueFacts = 0;
            this.falseFacts = 0;
            this.trueDetectedFacts = 0;
            this.falseDetectedFacts = 0;
        }

        public TrueAndFalseCounter(int allFacts, int trueFacts, int falseFacts, int trueDetectedFacts, int falseDetectedFacts) {
            this.allFacts = allFacts;
            this.trueFacts = trueFacts;
            this.falseFacts = falseFacts;
            this.trueDetectedFacts = trueDetectedFacts;
            this.falseDetectedFacts = falseDetectedFacts;
        }

        public void addAllFacts () {
            this.allFacts = this.allFacts +1;
        }

        public void addTrueFacts () {
            this.trueFacts = this.trueFacts +1;
        }

        public void addFalseFacts () {
            this.falseFacts = this.falseFacts +1;
        }

        public void addTrueDetectedFacts () {
            this.trueDetectedFacts = this.trueDetectedFacts +1;
        }

        public void addFalseDetectedFacts () {
            this.falseDetectedFacts = this.falseDetectedFacts +1;
        }

        public void addTT () {
            this.TT = this.TT +1;
        }

        public void addTF () {
            this.TF = this.TF +1;
        }

        public void addFT () {
            this.FT = this.FT +1;
        }

        public void addFF () {
            this.FF = this.FF +1;
        }
    }
}
