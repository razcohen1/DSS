package app.algorithm.implementation;

import app.algorithm.CityTraverseAlgorithm;
import app.algorithm.services.SimulatedAnnealingMaximumScorePathFinder;
import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
@ConditionalOnProperty(value = "algorithm.implementation", havingValue = "sa")
public class SimulatedAnnealingCityTraverse implements CityTraverseAlgorithm {
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;
    private SimulatedAnnealingMaximumScorePathFinder pathFinder = new SimulatedAnnealingMaximumScorePathFinder();

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        problemInput.setZeroScoreStreets(new ArrayList<>());
        problemInput.getMissionProperties().setTimeAllowedForCarsItinerariesInSeconds(10000);
        pathFinder.setMaximumRunningTimeInSeconds(maximumRunningTime);
        Path path = pathFinder.find(problemInput);
        return ProblemOutput.builder().bestPaths(Collections.singletonList(path)).totalScore(path.getScore()).build();
    }
}
