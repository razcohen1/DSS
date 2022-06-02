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

@Getter
@Setter
@Service
@ConditionalOnProperty(value = "algorithm.deterministic", havingValue = "false")
public class AlgorithmImplWithoutSavingPathsProbability implements Algorithm {
    private BestPathFinderByReference bestPathFinder = new BestPathFinderByReference();
    @Value(value = "${best.of:100}")
    private int iterations;

    //TODO: the problem with the bidirectional streets where they show up twice in the json and therefore passing them
    // in both directions gives points
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
        bestPathFinder.setProbabiltyToReplaceBest(0.2);
        List<PathDetails> bestPaths = new ArrayList<>();
        List<List<Street>> bestCarsPaths = new ArrayList<>();
        List<Street> traversedAlreadyStreets = new ArrayList<>();
        for (int carIndex = 0; carIndex < problemInput.getMissionProperties().getAmountOfCars(); carIndex++) {
            PathDetails bestPath = bestPathFinder.findBestPath(problemInput, traversedAlreadyStreets);
            bestCarsPaths.add(bestPath.getStreets());
            bestPaths.add(bestPath);
            traversedAlreadyStreets.addAll(bestPath.getStreets());
            bestPath.getStreets().forEach(street -> {
                if (!street.isOneway())
                    traversedAlreadyStreets.add(problemInput.getStreetToInverseStreet().get(street));
            });
            bestPathFinder.setProbabiltyToReplaceBest(bestPathFinder.getProbabiltyToReplaceBest() + 0.2);
        }

        return ProblemOutput.builder().bestPaths(bestPaths).bestCarsPaths(bestCarsPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }

    private Double calculateTotalScore(List<PathDetails> bestPaths) {
        return bestPaths.stream().map(PathDetails::getScore).reduce((double) 0, Double::sum);
    }
}
