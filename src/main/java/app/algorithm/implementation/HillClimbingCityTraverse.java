package app.algorithm.implementation;

import app.algorithm.CityTraverseAlgorithm;
import app.algorithm.services.HillClimbingMaximalPathFinder;
import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.algorithm.services.StreetsScorer.calculateTotalScore;
import static app.algorithm.services.StreetsScorer.getZeroScoreStreetsFromPath;

@Service
@ConditionalOnProperty(value = "algorithm.implementation", havingValue = "hillclimbing")
public class HillClimbingCityTraverse implements CityTraverseAlgorithm {
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;
    @Value(value = "${number.of.restarts:100}")
    private int numberOfRestarts;
    private HillClimbingMaximalPathFinder hillClimbingMaximalPathFinder = new HillClimbingMaximalPathFinder();

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        List<Path> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        problemInput.setZeroScoreStreets(zeroScoreStreets);
        hillClimbingMaximalPathFinder.setMaximumRunningTimeInSeconds(calculateRunningTimePerCall(problemInput));
        int amountOfCars = problemInput.getMissionProperties().getAmountOfCars();
        Path bestPath;
        for (int carIndex = 0; carIndex < amountOfCars; carIndex++) {
            bestPath = findBestPathOutOfAllRestarts(problemInput);
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
        }

        return ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }

    private Path findBestPathOutOfAllRestarts(ProblemInput problemInput) {
        Path bestPath;
        Path path;
        bestPath = Path.builder().score(0).build();
        for (int i = 0; i < numberOfRestarts; i++) {
            path = hillClimbingMaximalPathFinder.find(problemInput);
            if (path.getScore() > bestPath.getScore())
                bestPath = path;
        }
        return bestPath;
    }

    private double calculateRunningTimePerCall(ProblemInput problemInput) {
        return maximumRunningTime / (problemInput.getMissionProperties().getAmountOfCars() * numberOfRestarts);
    }
}
