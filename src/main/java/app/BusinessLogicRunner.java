package app;

import app.algorithm.Algorithm;
import app.files.JsonReader;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.ProceedableJunction;
import app.visualization.ProblemOutputVisualizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;

import static app.algorithm.services.InverseStreetFinder.createStreetToInverseStreetMap;
import static app.algorithm.services.JunctionToProceedableJunctionsCreator.createJunctionToProceedableJunctionsMap;

@Service
public class BusinessLogicRunner {
    @Value(value = "${input.filepath}")
    private String inputFilepath;
    @Autowired
    private Algorithm algorithm;
    @Autowired
    private ProblemOutputVisualizer visualizer;
    private JsonReader jsonReader = new JsonReader();

    @PostConstruct
    public void postConstruct() {
        run();
    }

    public void run() {
        ProblemInput problemInput = jsonReader.read(inputFilepath, ProblemInput.class);
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctionsMap = createJunctionToProceedableJunctionsMap(problemInput.getJunctionsList(), problemInput.getStreetsList());
        problemInput.setJunctionToProceedableJunctions(junctionToProceedableJunctionsMap);
        problemInput.setStreetToInverseStreet(createStreetToInverseStreetMap(problemInput.getStreetsList(), junctionToProceedableJunctionsMap));
        ProblemOutput problemOutput = algorithm.run(problemInput);
        System.out.println(problemOutput.getTotalScore());
        visualizer.visualize(problemInput, problemOutput);
    }
}
