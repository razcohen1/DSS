package app.algorithm.implementation;

import app.algorithm.services.MaximumScorePathFinder;
import app.files.JsonReader;
import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static app.algorithm.implementation.HillClimbingTraverseAlgorithmTest.assertPathsAreValid;
import static app.algorithm.services.InverseStreetFinder.createStreetToInverseStreetMap;
import static app.algorithm.services.ProceedableStreetsCalculator.createJunctionToProceedableStreetsMap;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProbabilisticCityTraverseAlgorithmTest {
    private ProbabilisticCityTraverseAlgorithm algorithm;
    private JsonReader jsonReader;
    private final String inputJsonFilepath = "src\\test\\resources\\input_example_1.json";
    private ProblemInput problemInput;

    @Before
    public void setUp() {
        jsonReader = new JsonReader();
        algorithm = ProbabilisticCityTraverseAlgorithm.builder()
                .maximumRunningTime(5)
                .iterations(100)
                .maximumScorePathFinder(MaximumScorePathFinder.builder()
                        .dropEarlyPathsThatCantBeatBestScoreBy(0)
                        .build())
                .build();
        problemInput = jsonReader.read(inputJsonFilepath, ProblemInput.class);
        MultiValueMap<Long, Street> junctionToProceedableStreetsMap = createJunctionToProceedableStreetsMap(problemInput.getJunctionsList(), problemInput.getStreetsList());
        problemInput.setJunctionToProceedableStreets(junctionToProceedableStreetsMap);
        problemInput.setStreetToInverseStreet(createStreetToInverseStreetMap(problemInput.getStreetsList(), junctionToProceedableStreetsMap));
    }

    @Test
    public void algorithmReturnsValidOutputTest() {
        ProblemOutput problemOutput = algorithm.run(problemInput);
        List<Path> bestPaths = problemOutput.getBestPaths();
        assertThat(bestPaths.size(), is(problemInput.getMissionProperties().getAmountOfCars()));
        assertTrue(problemOutput.getTotalScore() > 0.5 * problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds() * problemInput.getMissionProperties().getAmountOfCars());
        assertPathsAreValid(bestPaths);
    }
}