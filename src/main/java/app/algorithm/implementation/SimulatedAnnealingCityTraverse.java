package app.algorithm.implementation;

import app.algorithm.CityTraverseAlgorithm;
import app.algorithm.services.SimulatedAnnealingMaximumScorePathFinder;
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
public class SimulatedAnnealingCityTraverse implements CityTraverseAlgorithm {
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;
    @Value(value = "${number.of.restarts:100}")
    private int numberOfRestarts;
    private SimulatedAnnealingMaximumScorePathFinder pathFinder = new SimulatedAnnealingMaximumScorePathFinder();

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        List<Path> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        pathFinder.setMaximumRunningTimeInSeconds(maximumRunningTime / (problemInput.getMissionProperties().getAmountOfCars() * numberOfRestarts));
        Path bestPath = Path.builder().score(0).build();
        problemInput.setZeroScoreStreets(zeroScoreStreets);
        for (int carIndex = 0; carIndex < problemInput.getMissionProperties().getAmountOfCars(); carIndex++) {
            bestPath = Path.builder().score(0).build();
//            problemInput.setZeroScoreStreets(new ArrayList<>(zeroScoreStreets));
            for (int rerunIndex = 0; rerunIndex < numberOfRestarts; rerunIndex++) {
                Path path = pathFinder.find(problemInput);
                if (path.getScore() > bestPath.getScore())
                    bestPath = path;
            }
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
        }

        ProblemOutput build = ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
        return build;
    }
}
