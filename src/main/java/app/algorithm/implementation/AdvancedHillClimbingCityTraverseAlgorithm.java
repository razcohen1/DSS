package app.algorithm.implementation;

import app.algorithm.CityTraverseAlgorithm;
import app.algorithm.services.HillClimbingMaximalPathFinder;
import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.algorithm.services.StreetsScorer.calculateTotalScore;
import static app.algorithm.services.StreetsScorer.getZeroScoreStreetsFromPath;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Service
@ConditionalOnProperty(value = "algorithm.implementation", havingValue = "advancedhillclimbing")
public class AdvancedHillClimbingCityTraverseAlgorithm implements CityTraverseAlgorithm {
    @Value(value = "${maximum.running.time.wanted.in.seconds:30}")
    private double maximumRunningTime;
    @Autowired
    private HillClimbingMaximalPathFinder hillClimbingMaximalPathFinder;

    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        List<Path> bestPaths = new ArrayList<>();
        List<Street> zeroScoreStreets = new ArrayList<>();
        problemInput.setZeroScoreStreets(zeroScoreStreets);
        int amountOfCars = problemInput.getMissionProperties().getAmountOfCars();
        hillClimbingMaximalPathFinder.setMaximumRunningTimeInSeconds(maximumRunningTime/ amountOfCars);
        Path bestPath;
        for (int carIndex = 0; carIndex < amountOfCars; carIndex++) {
            bestPath = hillClimbingMaximalPathFinder.find(problemInput);
            bestPaths.add(bestPath);
            zeroScoreStreets.addAll(getZeroScoreStreetsFromPath(bestPath, streetToInverseStreet));
        }

        return ProblemOutput.builder().bestPaths(bestPaths).totalScore(calculateTotalScore(bestPaths)).build();
    }

}
