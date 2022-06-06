package app;

import app.algorithm.CityTraverseAlgorithm;
import app.files.JsonReader;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import app.visualization.ProblemOutputVisualizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;

import static app.algorithm.services.InverseStreetFinder.createStreetToInverseStreetMap;
import static app.algorithm.services.ProceedableStreetsCalculator.createJunctionToProceedableStreetsMap;

@Service
public class BusinessLogicRunner {
    @Value(value = "${input.filepath}")
    private String inputFilepath;
    @Autowired
    private CityTraverseAlgorithm cityTraverseAlgorithm;
    @Autowired
    private ProblemOutputVisualizer visualizer;
    private JsonReader jsonReader = new JsonReader();

    @PostConstruct
    public void postConstruct() {
        run();
    }

    public void run() {
        ProblemInput problemInput = jsonReader.read(inputFilepath, ProblemInput.class);
        MultiValueMap<Long, Street> junctionToProceedableStreetsMap = createJunctionToProceedableStreetsMap(problemInput.getJunctionsList(), problemInput.getStreetsList());
        problemInput.setJunctionToProceedableStreets(junctionToProceedableStreetsMap);
        problemInput.setStreetToInverseStreet(createStreetToInverseStreetMap(problemInput.getStreetsList(), junctionToProceedableStreetsMap));
        ProblemOutput problemOutput = cityTraverseAlgorithm.run(problemInput);
        System.out.println("total score: " + problemOutput.getTotalScore());
        visualizer.visualize(problemInput, problemOutput);
    }
}
