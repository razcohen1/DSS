package app.algorithm.implementation;

import app.algorithm.Algorithm;
import app.algorithm.services.BestPathFinderByReference;
import app.model.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
@ConditionalOnProperty(value = "algorithm.deterministic", havingValue = "true")
public class AlgorithmImplWithoutSavingPaths implements Algorithm {
    private BestPathFinderByReference bestPathFinder = new BestPathFinderByReference();

    //TODO: the problem with the bidirectional streets where they show up twice in the json and therefore passing them
    // in both directions gives points
    @Override
    public ProblemOutput run(ProblemInput problemInput) {
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
        }
        double totalScore = bestPaths.stream().map(PathDetails::getScore).reduce((double) 0, Double::sum);
        System.out.println(totalScore);
        return ProblemOutput.builder().bestPaths(bestPaths).bestCarsPaths(bestCarsPaths).totalScore(totalScore).build();
    }
}
