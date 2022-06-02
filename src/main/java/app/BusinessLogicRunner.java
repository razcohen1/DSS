package app;

import app.algorithm.Algorithm;
import app.algorithm.implementation.AlgorithmImplWithoutSavingPaths;
import app.algorithm.services.InverseStreetFinder;
import app.algorithm.services.JunctionToProceedableJunctionsCreator;
import app.files.JsonReader;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.ProceedableJunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;

import static app.algorithm.services.InverseStreetFinder.createStreetToInverseStreetMap;
import static app.algorithm.services.JunctionToProceedableJunctionsCreator.createJunctionToProceedableJunctionsMap;
import static app.visualization.ProblemOutputVisualizer.visualize;

@Service
public class BusinessLogicRunner {
    @Value(value = "${input.filepath}")
    private String inputFilepath;
    @Autowired
    private Algorithm algorithm;
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
        visualize(problemInput, problemOutput);
    }
}
