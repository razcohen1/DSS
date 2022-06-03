package app.algorithm.implementation;

import app.algorithm.Algorithm;
import app.algorithm.services.BestPathFinderByReference;
import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Service
@ConditionalOnProperty(value = "algorithm.deterministic", havingValue = "false")
public class AlgorithmImplProbabilistic implements Algorithm {
    private BestPathFinderByReference bestPathFinder = new BestPathFinderByReference();
    @Value(value = "${best.of:100}")
    private int iterations;

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        ProblemOutput best = ProblemOutput.builder().totalScore(0).build();
        ProblemOutput currentOutput;
        for (int i = 0; i < iterations; i++) {
            currentOutput = runOnce(problemInput);
            if (currentOutput.getTotalScore() > best.getTotalScore())
                best = currentOutput;
        }

        System.out.println("best = " + best.getTotalScore());
        return best;
    }

    private ProblemOutput runOnce(ProblemInput problemInput) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        List<PathDetails> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        PathDetails bestPath;
        bestPathFinder.setProbabiltyToReplaceBest(0.2);
        for (int carIndex = 0; carIndex < getAmountOfCars(problemInput); carIndex++) {
            problemInput.setZeroScoreStreets(zeroScoreStreets);
            bestPath = bestPathFinder.findBestPath(problemInput);
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
            bestPathFinder.setProbabiltyToReplaceBest(bestPathFinder.getProbabiltyToReplaceBest() + 0.2);
        }

        return ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }

    private int getAmountOfCars(ProblemInput problemInput) {
        return problemInput.getMissionProperties().getAmountOfCars();
    }

    private List<Street> getZeroScoreStreetsFromPath(PathDetails bestPath, Map<Street, Street> streetToInverseStreet) {
        List<Street> streets = bestPath.getStreets();
        List<Street> zeroScoreStreetsFromCurrentRun = new ArrayList<>(streets);
        streets.forEach(street -> {
            if (!street.isOneway()) {
                zeroScoreStreetsFromCurrentRun.add(streetToInverseStreet.get(street));
            }
        });
        return zeroScoreStreetsFromCurrentRun;
    }

    private Double calculateTotalScore(List<PathDetails> bestPaths) {
        return bestPaths.stream().map(PathDetails::getScore).reduce((double) 0, Double::sum);
    }
}
