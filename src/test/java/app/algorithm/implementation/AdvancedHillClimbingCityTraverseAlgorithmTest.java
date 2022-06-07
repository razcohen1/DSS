package app.algorithm.implementation;

import app.algorithm.services.HillClimbingMaximalPathFinder;
import app.algorithm.services.MaximumScorePathFinder;
import app.algorithm.services.neighbor.generator.implementation.AdvancedHillClimbingNeighborGenerator;
import app.files.JsonReader;
import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static app.algorithm.services.InverseStreetFinder.createStreetToInverseStreetMap;
import static app.algorithm.services.ProceedableStreetsCalculator.createJunctionToProceedableStreetsMap;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AdvancedHillClimbingCityTraverseAlgorithmTest {
    private AdvancedHillClimbingCityTraverseAlgorithm algorithm;
    private JsonReader jsonReader;
    private final String inputJsonFilepath = "src\\test\\resources\\input_example_1.json";
    private ProblemInput problemInput;

    @Before
    public void setUp() {
        jsonReader = new JsonReader();
        algorithm = createAlgorithmInstance();
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

    public static void assertPathsAreValid(List<Path> bestPaths) {
        for (Path bestPath : bestPaths) {
            List<Street> streets = bestPath.getStreets();
            for (int j = 0; j < streets.size() - 1; j++) {
                assertThat(streets.get(j).getJunctionToId(), is(streets.get(j + 1).getJunctionFromId()));
            }
        }
    }

    private AdvancedHillClimbingCityTraverseAlgorithm createAlgorithmInstance() {
        return AdvancedHillClimbingCityTraverseAlgorithm.builder()
                .maximumRunningTime(10)
                .hillClimbingMaximalPathFinder(HillClimbingMaximalPathFinder.builder()
                        .neighborGenerator(AdvancedHillClimbingNeighborGenerator.builder()
                                .maximumScorePathForwardFinder(MaximumScorePathFinder.builder()
                                        .numberOfStreetsForwardToCheck(20)
                                        .probabilityToReplaceBest(1)
                                        .checkLimitedNumberOfStreetsForward(true)
                                        .maximumRunningTimeInSeconds(Integer.MAX_VALUE)
                                        .build())
                                .build())
                        .build())
                .build();
    }
}