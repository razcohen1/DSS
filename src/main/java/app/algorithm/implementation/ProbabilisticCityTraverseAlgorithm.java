package app.algorithm.implementation;

import app.algorithm.CityTraverseAlgorithm;
import app.algorithm.services.MaximumScorePathFinder;
import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.algorithm.services.StreetsScorer.calculateTotalScore;
import static app.algorithm.services.StreetsScorer.getZeroScoreStreetsFromPath;

@Getter
@Setter
@Service
@ConditionalOnProperty(value = "algorithm.implementation", havingValue = "probabilistic")
public class ProbabilisticCityTraverseAlgorithm implements CityTraverseAlgorithm {
    @Autowired
    private MaximumScorePathFinder maximumScorePathFinder;
    @Value(value = "${best.of:100}")
    private int iterations;
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;
    private final double initialProbabilityToReplaceBest = 0.2;

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        maximumScorePathFinder.setMaximumRunningTimeInSeconds(calculateMaximumRunningTimePerCall(problemInput));
        ProblemOutput best = ProblemOutput.builder().totalScore(0).build();
        ProblemOutput currentOutput;
        for (int i = 0; i < iterations; i++) {
            currentOutput = runOnce(problemInput);
            if (currentOutput.getTotalScore() > best.getTotalScore())
                best = currentOutput;
        }

        return best;
    }

    private ProblemOutput runOnce(ProblemInput problemInput) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        List<Path> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        problemInput.setZeroScoreStreets(zeroScoreStreets);
        maximumScorePathFinder.setProbabilityToReplaceBest(initialProbabilityToReplaceBest);
        double probabilityRampUp = calculateProbabilityRampUpPerCar(problemInput);
        Path bestPath;
        for (int carIndex = 0; carIndex < getAmountOfCars(problemInput); carIndex++) {
            bestPath = maximumScorePathFinder.find(problemInput);
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
            maximumScorePathFinder.setProbabilityToReplaceBest(maximumScorePathFinder.getProbabilityToReplaceBest() + probabilityRampUp);
        }

        return ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }

    private double calculateProbabilityRampUpPerCar(ProblemInput problemInput) {
        return (1 - initialProbabilityToReplaceBest) / (getAmountOfCars(problemInput) - 1);
    }

    private double calculateMaximumRunningTimePerCall(ProblemInput problemInput) {
        return maximumRunningTime / (iterations * getAmountOfCars(problemInput));
    }

    private int getAmountOfCars(ProblemInput problemInput) {
        return problemInput.getMissionProperties().getAmountOfCars();
    }
}
