package app.algorithm;

import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.algorithm.InverseStreetFinder.findInverseStreet;

@Getter
@Setter
public class AlgorithmStreetsZeroing implements Algorithm {
    private NewPathFinder newPathFinder = new NewPathFinder();

    //TODO: the problem with the bidirectional streets where they show up twice in the json and therefore passing them
    // in both directions gives points
    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        List<List<Street>> bestCarsPaths = new ArrayList<>();
        Map<Street,Boolean> streetToIsZeroScore = new HashMap<>();
//        List<Street> traversedAlreadyStreets = new ArrayList<>();
        for (int carIndex = 0; carIndex < problemInput.getMissionProperties().getAmountOfCars(); carIndex++) {
            PathDetails bestPath = newPathFinder.findBestPath(problemInput, streetToIsZeroScore);
            bestCarsPaths.add(bestPath.getStreets());
            bestPath.getStreets().forEach(street -> {
                streetToIsZeroScore.put(street,true);
                if (!street.isOneway())
                    streetToIsZeroScore.put(findInverseStreet(street, problemInput.getJunctionToProceedableJunctions()),true);
            });
//            traversedAlreadyStreets.addAll(bestPath.getStreets());
//            bestPath.getStreets().forEach(street -> {
//                if (!street.isOneway())
//                    traversedAlreadyStreets.add(findInverseStreet(street, problemInput.getJunctionToProceedableJunctions()));
//            });
        }
        return ProblemOutput.builder().bestCarsPaths(bestCarsPaths).build();
    }
}

