package org.diceresearch.KGV.Pipeline.FactBenchPipeline;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.diceresearch.KGV.ETL.Extract.FileExtractorOutOfPackage;
import org.diceresearch.KGV.ETL.Transform.RDFModelTransform;
import org.diceresearch.KGV.utility.TripleExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



// get a file with this format
/*1
        factbench/factbench/test/correct/death/death_00107.ttl
        factCheckScore: 0.25 setProofSentences : [] subject : Jacqueline Kennedy Onassis object : New York City predicate deathPlace
        -=-=-=-=-=-=-==-=-==-=-=-=-==-=-=-=-=-=-=-==-=-=-=-

        Generate file with this format
        factbench/factbench/test/correct/death/death_00107.ttl  0.25

        then convert to formay for using in Gerbil
        like this
        <http://swc2017.aksw.org/task2/dataset/property-1001> <http://swc2017.aksw.org/hasTruthValue> "0.0"^^<http://www.w3.org/2001/XMLSchema#double> .
<http://swc2017.aksw.org/task2/dataset/property-1001> <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> <http://dbpedia.org/resource/Vladimir_Putin> .
<http://swc2017.aksw.org/task2/dataset/property-1001> <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://dbpedia.org/ontology/subsidiary> .
<http://swc2017.aksw.org/task2/dataset/property-1001> <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://dbpedia.org/resource/Russia> .
<http://swc2017.aksw.org/task2/dataset/property-1001> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .

id should serach from refrence file
subject object and predicate read from ttl file in fact bench
also load coppal result as model and retrive score then put the maximum (between factcheck and coppal)
 */
public class GenerateFacadeResult {
    public static String ForProcessPath = "/home/farshad/repos/factBench/";
    public static String ForProcessFileName = "factcheckResultCorrect.txt";
    //public static String ForProcessFileName = "factcheckResultWrongProperty.txt";
    //public static String ForProcessFileName = "factcheckResultWrongMix.txt";

    static String refrenceFilePath = "/home/farshad/repos/factBench/factbench/test/correct/true.nt";
    //static String refrenceFilePath = "/home/farshad/repos/factBench/factbench/test/wrong/property/property.nt";
    //static String refrenceFilePath = "/home/farshad/repos/factBench/factbench/test/wrong/mix/fb_mix.nt";

    static String vt = "true";
    static String pathlen = "3";
    static String copaalRefrenceFilePath = "/home/farshad/Desktop/deliverable results Gerbil/RESULT_VT_true_pathLen_3.nt";

    public static void main(String[] args) throws Exception {
        Run();
    }

    public static void Run() throws Exception {
        FileExtractorOutOfPackage extractor = new FileExtractorOutOfPackage();
        File file = extractor.Extract(ForProcessPath+ForProcessFileName);

        File refrence = (File) extractor.Extract(refrenceFilePath);
        RDFModelTransform rdfModelTransform = new RDFModelTransform("nt");
        Model model= rdfModelTransform.Transform(refrence,"");

        File copaalFile = (File) extractor.Extract(copaalRefrenceFilePath);
        Model copaalModel= rdfModelTransform.Transform(copaalFile,"");


        Scanner scanner = new Scanner(file);
        String line = "";

        while (scanner.hasNextLine()) {
            // this is the number
            line = scanner.nextLine();

            // this is the adress
            line = scanner.nextLine();
            String path = line;

            // this is the result
            line = scanner.nextLine();
            String[] parts = line.split(" ");
            Double score = Double.parseDouble(parts[2]);

            // this is the seperator  -=-=-=-=-=-=-==-=-==-=-=-=-==-=-=-=-=-=-=-==-=-=-=-
            line = scanner.nextLine();

            System.out.println(path+"/t"+score);

            // Convert to format

            String completePath = "/home/farshad/repos/"+path.replaceFirst("factbench","factBench");
            System.out.println(completePath);
            File ttlFile = extractor.Extract(completePath);
            String fileData = ReadFile(ttlFile);
            TripleExtractor tripleExtractor = new TripleExtractor(fileData, false);
            String id = ProcessTheResultConvertToFormat.searchId(model,tripleExtractor.getSubjectUri().replace("&","%26") ,tripleExtractor.getPredicateUri().replace("&","%26") ,tripleExtractor.getObjectUri().replace("&","%26") );
            double copaalScore = queryCopaalScore(id,copaalModel);
            score = Math.max(copaalScore,score);
            ProcessTheResultConvertToFormat.writeResult(id,tripleExtractor.getSubjectUri().replace("&","%26") ,tripleExtractor.getPredicateUri().replace("&","%26") ,tripleExtractor.getObjectUri().replace("&","%26"),score,ForProcessPath+"Formatted_Facade_"+vt+"_" +pathlen+"_"+ForProcessFileName);

        }

        scanner.close();
    }

    private static double queryCopaalScore(String id, Model copaalModel) {
        String query = "select ?s WHERE { <" +
                id+"> <http://swc2017.aksw.org/hasTruthValue> ?s."+
                "}";

        ResultSet result = QueryExecutionFactory.create( query, copaalModel ).execSelect();

        if (result.hasNext()) {
            QuerySolution qs = result.next();

            String s = qs.get("?s").toString();
            s = s.replace("^^http://www.w3.org/2001/XMLSchema#double","");
            return Double.parseDouble(s);
        }

        //throw new Exception(" Can not find ID subject"+subject+" predicate "+ predicate + " object "+ object);
        System.out.println(" Can not find score "+id);
        return 0.0;
    }

    private static String ReadFile(File input) throws FileNotFoundException {
        Scanner myReader = new Scanner(input);
        StringBuilder sb = new StringBuilder();
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            sb.append(data);
        }
        myReader.close();
        return sb.toString();
    }
}
