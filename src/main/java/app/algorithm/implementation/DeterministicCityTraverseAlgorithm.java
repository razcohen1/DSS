package app.algorithm.implementation;

import app.algorithm.CityTraverseAlgorithm;
import app.algorithm.services.MaximumScorePathFinder;
import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import lombok.*;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Service
@ConditionalOnProperty(value = "algorithm.implementation", havingValue = "deterministic", matchIfMissing = true)
public class DeterministicCityTraverseAlgorithm implements CityTraverseAlgorithm {
    @Autowired
    private MaximumScorePathFinder maximumScorePathFinder;
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        List<Path> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        problemInput.setZeroScoreStreets(zeroScoreStreets);
        int amountOfCars = problemInput.getMissionProperties().getAmountOfCars();
        maximumScorePathFinder.setProbabilityToReplaceBest(1);
        maximumScorePathFinder.setMaximumRunningTimeInSeconds(maximumRunningTime/amountOfCars);
        Path bestPath;
        for (int carIndex = 0; carIndex < amountOfCars; carIndex++) {
            bestPath = maximumScorePathFinder.find(problemInput);
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
        }

        return ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }
}
