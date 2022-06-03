package app.algorithm.implementation;

import app.algorithm.Algorithm;
import app.algorithm.services.BestPathFinder;
import app.model.PathDetails;
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
@ConditionalOnProperty(value = "algorithm.deterministic", havingValue = "false")
public class AlgorithmImplProbabilistic implements Algorithm {
    @Autowired
    private BestPathFinder bestPathFinder;
    @Value(value = "${best.of:100}")
    private int iterations;
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        bestPathFinder.setMaximumRunningTimeInSeconds(calculateMaximumRunningTimePerCall(problemInput));
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
        List<PathDetails> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        PathDetails bestPath;
        bestPathFinder.setProbabilityToReplaceBest(0.2);
        for (int carIndex = 0; carIndex < getAmountOfCars(problemInput); carIndex++) {
            problemInput.setZeroScoreStreets(zeroScoreStreets);
            bestPath = bestPathFinder.findBestPath(problemInput);
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
            bestPathFinder.setProbabilityToReplaceBest(bestPathFinder.getProbabilityToReplaceBest() + 0.2);
        }

        return ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }

    private double calculateMaximumRunningTimePerCall(ProblemInput problemInput) {
        return maximumRunningTime / (iterations * getAmountOfCars(problemInput));
    }

    private int getAmountOfCars(ProblemInput problemInput) {
        return problemInput.getMissionProperties().getAmountOfCars();
    }
}
