package org.diceresearch.KGV.Pipeline.Pipeline;

import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Extract.IExtractor;
import org.diceresearch.KGV.ETL.Model.SimpleRDFWithVeracity;
import org.diceresearch.KGV.ETL.Model.frockgResult;
import org.diceresearch.KGV.ETL.Transform.SimpleRdfSmartIdWithVeracityTransform;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateFacadeFromCOPAALandFactcheckGenerateGrundTruthPipeline {

    //http://dbpedia.org/resource/Deep_Impact_(film)	http://dbpedia.org/ontology/productionCompany	http://dbpedia.org/resource/David_Brown_(producer)	0.9952637156848194	20532600-6f78-4f59-9020-ca888c1e4747
    private static String FileNameValidCOPAAL = "/home/farshad/experiments/FAVEL/20122022/0shortResults_testCorrect13122022.txt";

    //http://dbpedia.org/resource/Deep_Impact_(film)	http://dbpedia.org/ontology/productionCompany	http://dbpedia.org/resource/David_Brown_(producer)	0.44954128440366975
    private static String FileNameValidFactCheck = "/home/farshad/experiments/FAVEL/20122022/resultIsValid_FactCheck_newFavelCorrect_FactCheck.txt";

    private static boolean isCorrect = true;

    private static String resultPath = "/home/farshad/experiments/FAVEL/20122022/results/";

    private static double factcheckThreshhold = 0.5;

    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws Exception {

        IExtractor extractor = new FileExtractorOutOfPackage();

        File copaalResult = (File) extractor.Extract(FileNameValidCOPAAL);
        File factCheckResult = (File) extractor.Extract(FileNameValidFactCheck);

        SimpleRdfSmartIdWithVeracityTransform rdfTransformer  = new SimpleRdfSmartIdWithVeracityTransform();

        List<SimpleRDFWithVeracity> copaalResultList = rdfTransformer.transform(copaalResult,"\t");
        List<SimpleRDFWithVeracity> factCheckResultList = rdfTransformer.transform(factCheckResult,"\t");

        List<frockgResult> frockgResults = new ArrayList<>();


        for(SimpleRDFWithVeracity copaalRDF : copaalResultList){
            for(SimpleRDFWithVeracity factcheckRDF : factCheckResultList){
                if(copaalRDF.sameTriple(factcheckRDF)){
                    double factCheckVeracity = factcheckRDF.getVeracity();
                    if(factCheckVeracity < factcheckThreshhold){factCheckVeracity = 0; }
                    frockgResult temp = new frockgResult(copaalRDF.getId(), copaalRDF.getSubject(), copaalRDF.getPredicate(), copaalRDF.getObject(), copaalRDF.getVeracity(), factCheckVeracity ,Math.max(copaalRDF.getVeracity(),factCheckVeracity), isCorrect);
                    // if does not contain a same triple then add it
                    // we use it because in one dataset we have duplicate of triples
                    boolean addToResult = true;
                    for(frockgResult fg : frockgResults){
                        if(temp.sameTriple(fg)){
                            addToResult = false;
                        }
                    }
                    if(addToResult) {
                        frockgResults.add(temp);
                    }
                }
            }
        }

        // print result

        for(frockgResult fg : frockgResults){
            writeResult(fg.getId(), fg.getSubject(), fg.getPredicate(), fg.getObject(),String.valueOf(fg.getFrockgVeracity()),resultPath+"_"+isCorrect+".rdf" ,resultPath+"groundTruth_"+isCorrect+".rdf" , isCorrect);
        }

    }

    private static void writeResult(String id, String subject, String predicate, String object, String veracity , String outPutDestination, String outPutDestinationGround ,boolean isTrue) throws IOException {
        StringBuilder sb = new StringBuilder();

        id = "<http://swc2017.aksw.org/task2/dataset/"+id+">";

        sb.append(id);

        sb.append(" <http://swc2017.aksw.org/hasTruthValue> \""+veracity+"\"^^<http://www.w3.org/2001/XMLSchema#double> .");
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

        try(FileWriter fw = new FileWriter(outPutDestination, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(sb.toString());
        } catch (IOException e) {
            throw e;
        }

        sb = new StringBuilder();

        sb.append(id);
        if(isTrue){
            sb.append(" <http://swc2017.aksw.org/hasTruthValue> \"1.0\"^^<http://www.w3.org/2001/XMLSchema#double> .");
            sb.append(System.lineSeparator());
        }else{
            sb.append(" <http://swc2017.aksw.org/hasTruthValue> \"0.0\"^^<http://www.w3.org/2001/XMLSchema#double> .");
            sb.append(System.lineSeparator());
        }
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

        try(FileWriter fw = new FileWriter(outPutDestinationGround, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(sb.toString());
        } catch (IOException e) {
            throw e;
        }
    }
}
