package app.algorithm;

import app.algorithm.best.JunctionToProceedableJunctionsCreator;
import app.files.JsonReader;
import app.model.ProblemInput;
import org.junit.Test;

public class AlgorithmImplTest {
    private AlgorithmImpl algorithm = new AlgorithmImpl();
    private JsonReader jsonReader = new JsonReader();

    @Test
    public void test() {
        ProblemInput problemInput = jsonReader.read("C:\\Users\\Raz\\Desktop\\DssProject\\DSS Project\\Input examples\\input_example_3.json", ProblemInput.class);
        problemInput.setJunctionToProceedableJunctions(JunctionToProceedableJunctionsCreator.createJunctionToProceedableJunctionsMap(problemInput.getJunctionsList(),problemInput.getStreetsList()));
        algorithm.run(problemInput);
    }
}