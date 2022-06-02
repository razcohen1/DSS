package app.algorithm.best;


import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProceedableJunction;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class BestPathFinder {
    private final double percentOptimizationOverPerformance = 100;
    @Setter
    private double probabiltyToReplaceBest = 1;

    public PathDetails findBestPath(ProblemInput problemInput, List<Street> zeroScoreStreets) {
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = problemInput.getJunctionToProceedableJunctions();
        long initialJunctionId = problemInput.getMissionProperties().getInitialJunctionId();
        double timeAllowedForCarsItinerariesInSeconds = problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
        PathDetails bestPath = PathDetails.builder().score(0).build();
        recursia(initialJunctionId, 0, timeAllowedForCarsItinerariesInSeconds, 0, bestPath,
                new ArrayList<>(), new ArrayList<>(), zeroScoreStreets, junctionToProceedableJunctions, streetToInverseStreet);
        return bestPath;
    }

    //todo: allow a limited amount of travels in the same street instead of only 1
    //todo: in dfs instead of contains use a boolean array or a map
    //todo: make sure im covering all the possible paths
    private void recursia(long currentJunction, double timePassed, double timeAllowed, double score, PathDetails bestPath,
                          List<Street> streets, List<Street> traveledAlready, List<Street> zeroScoreStreets,
                          MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions, Map<Street, Street> streetToInverseStreet) {
        boolean isTimeExceeded = false;
        double newScore;
        for (ProceedableJunction proceedableJunction : junctionToProceedableJunctions.get(currentJunction)) {
            if (!traveledAlready.contains(proceedableJunction.getStreet()) && canPassBestPath(score, timePassed, timeAllowed, bestPath)) {
                if (timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet() > timeAllowed) {
                    isTimeExceeded = true;
                } else {
                    traveledAlready.add(proceedableJunction.getStreet());
//                    if(!proceedableJunction.getStreet().isOneway())
//                        traveledAlready.add(findInverseStreet(proceedableJunction.getStreet(),junctionToProceedableJunctions));
                    ArrayList<Street> streetsWithTheNextStreet = new ArrayList<>(streets);
                    streetsWithTheNextStreet.add(proceedableJunction.getStreet());
                    newScore = score;
                    if (!zeroScoreStreets.contains(proceedableJunction.getStreet()))
                        newScore += proceedableJunction.getStreet().getRequiredTimeToFinishStreet();
                    List<Street> zeroScoreStreetsAfterStreet = new ArrayList<>(zeroScoreStreets);
                    zeroScoreStreetsAfterStreet.add(proceedableJunction.getStreet());
                    if (!proceedableJunction.getStreet().isOneway())
                        zeroScoreStreetsAfterStreet.add(streetToInverseStreet.get(proceedableJunction.getStreet()));
                    recursia(proceedableJunction.getJunctionId(),
                            timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet(), timeAllowed, newScore, bestPath,
                            streetsWithTheNextStreet, new ArrayList<>(traveledAlready), zeroScoreStreetsAfterStreet,
                            junctionToProceedableJunctions, streetToInverseStreet);
                }
            }
        }
        if (isTimeExceeded && Math.random()<probabiltyToReplaceBest) {
            if (score > bestPath.getScore()) {
                bestPath.setScore(score);
                bestPath.setStreets(streets);
                bestPath.setTime(timePassed);
            }
        }
    }

    private boolean canPassBestPath(double score, double timePassed, double timeAllowed, PathDetails bestPath) {
        return score + (timeAllowed - timePassed) > bestPath.getScore() + 100 * (100-percentOptimizationOverPerformance) /100;
    }
}
