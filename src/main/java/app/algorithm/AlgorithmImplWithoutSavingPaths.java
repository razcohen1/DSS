package app.algorithm;

import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static app.algorithm.InverseStreetFinder.findInverseStreet;

@Getter
@Setter
public class AlgorithmImplWithoutSavingPaths implements Algorithm {
    private BestPathFinder bestPathFinder = new BestPathFinder();

    //TODO: the problem with the bidirectional streets where they show up twice in the json and therefore passing them
    // in both directions gives points
    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        List<List<Street>> bestCarsPaths = new ArrayList<>();
        List<Street> traversedAlreadyStreets = new ArrayList<>();
        for (int carIndex = 0; carIndex < problemInput.getMissionProperties().getAmountOfCars(); carIndex++) {
            PathDetails bestPath = bestPathFinder.findBestPath(problemInput, traversedAlreadyStreets);
            bestCarsPaths.add(bestPath.getStreets());
            traversedAlreadyStreets.addAll(bestPath.getStreets());
            bestPath.getStreets().forEach(street -> {
                if (!street.isOneway())
                    traversedAlreadyStreets.add(findInverseStreet(street, problemInput.getJunctionToProceedableJunctions()));
            });
        }
        return ProblemOutput.builder().bestCarsPaths(bestCarsPaths).build();
    }
}
