package app.algorithm;

import app.files.JsonReader;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static app.visualization.ProblemOutputVisualizer.visualize;

@Service
public class BusinessLogicRunner {
//    private Algorithm algorithm = new AlgorithmImpl();
    private Algorithm algorithm = new AlgorithmImplWithoutSavingPaths();
    private JsonReader jsonReader = new JsonReader();

    //TODO: use current best to eliminate bad paths early
    @PostConstruct
    public void run(){
        ProblemInput problemInput = jsonReader.read("C:\\Users\\Raz\\Desktop\\DssProject\\DSS Project\\Input examples\\input_example_3.json", ProblemInput.class);
        problemInput.setJunctionToProceedableJunctions(JunctionToProceedableJunctionsCreator.createJunctionToProceedableJunctionsMap(problemInput.getJunctionsList(), problemInput.getStreetsList()));
        ProblemOutput problemOutput = algorithm.run(problemInput);
        visualize(problemInput, problemOutput);
    }
}
