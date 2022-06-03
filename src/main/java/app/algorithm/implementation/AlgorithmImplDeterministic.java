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
@ConditionalOnProperty(value = "algorithm.deterministic", havingValue = "true", matchIfMissing = true)
public class AlgorithmImplDeterministic implements Algorithm {
    @Autowired
    private BestPathFinder bestPathFinder;
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        List<PathDetails> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        int amountOfCars = problemInput.getMissionProperties().getAmountOfCars();
        bestPathFinder.setProbabilityToReplaceBest(1);
        bestPathFinder.setMaximumRunningTimeInSeconds(maximumRunningTime/amountOfCars);
        for (int carIndex = 0; carIndex < amountOfCars; carIndex++) {
            problemInput.setZeroScoreStreets(zeroScoreStreets);
            PathDetails bestPath = bestPathFinder.findBestPath(problemInput);
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
        }

        return ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }
}
