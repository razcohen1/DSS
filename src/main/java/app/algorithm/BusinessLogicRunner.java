package app.algorithm;

import app.files.JsonReader;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.ProceedableJunction;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;

import static app.visualization.ProblemOutputVisualizer.visualize;

@Service
public class BusinessLogicRunner {
//    private Algorithm algorithm = new AlgorithmImpl();
    private Algorithm algorithm = new AlgorithmImplWithoutSavingPaths();
//    private Algorithm algorithm = new AlgorithmStreetsZeroing();
    private JsonReader jsonReader = new JsonReader();

    //TODO: use current best to eliminate bad paths early
    @PostConstruct
    public void run(){
        ProblemInput problemInput = jsonReader.read("C:\\Users\\Raz\\Desktop\\DssProject\\DSS Project\\Input examples\\input_example_3.json", ProblemInput.class);
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctionsMap = JunctionToProceedableJunctionsCreator.createJunctionToProceedableJunctionsMap(problemInput.getJunctionsList(), problemInput.getStreetsList());
        problemInput.setJunctionToProceedableJunctions(junctionToProceedableJunctionsMap);
        problemInput.setStreetToInverseStreet(InverseStreetFinder.createStreetToInverseStreetMap(problemInput.getStreetsList(),junctionToProceedableJunctionsMap));
        ProblemOutput problemOutput = algorithm.run(problemInput);
        visualize(problemInput, problemOutput);
    }
}
