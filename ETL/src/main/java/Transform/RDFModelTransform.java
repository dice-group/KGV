package Transform;

import java.io.File;
import java.io.FileInputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RDFModelTransform implements ITransform<Model, File>{
    @Override
    public Model Transform(File input, String splitter) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(input), "", "N-TRIPLE");
        return model;
    }
}
