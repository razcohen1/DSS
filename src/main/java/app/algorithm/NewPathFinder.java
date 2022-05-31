package app.algorithm;


import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.algorithm.InverseStreetFinder.findInverseStreet;

public class NewPathFinder {
    public PathDetails findBestPath(ProblemInput problemInput, Map<Street, Boolean> streetToIsZeroScore) {
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = problemInput.getJunctionToProceedableJunctions();
        long initialJunctionId = problemInput.getMissionProperties().getInitialJunctionId();
        double timeAllowedForCarsItinerariesInSeconds = problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
        PathDetails bestPath = PathDetails.builder().score(0).build();
        recursia(initialJunctionId, 0, timeAllowedForCarsItinerariesInSeconds, 0, bestPath,
                new ArrayList<>(), new HashMap<>(), streetToIsZeroScore, junctionToProceedableJunctions);
        return bestPath;
    }

    //todo: allow a limited amount of travels in the same street instead of only 1
    //todo: in dfs instead of contains use a boolean array or a map
    //todo: make sure im covering all the possible paths
    private void recursia(long currentJunction, double timePassed, double timeAllowed, double score, PathDetails bestPath,
                          List<Street> streets, Map<Street, Boolean> traveledAlready, Map<Street, Boolean> streetToIsZeroScore,
                          MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions) {
        boolean isTimeExceeded = false;
        double newScore;
        for (ProceedableJunction proceedableJunction : junctionToProceedableJunctions.get(currentJunction)) {
            if (traveledAlready.get(proceedableJunction.getStreet()) == null && canPassBestPath(score, timePassed, timeAllowed, bestPath)) {
                if (timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet() > timeAllowed) {
                    isTimeExceeded = true;
                } else {
                    traveledAlready.put(proceedableJunction.getStreet(), true);
//                    if(!proceedableJunction.getStreet().isOneway())
//                        traveledAlready.add(findInverseStreet(proceedableJunction.getStreet(),junctionToProceedableJunctions));
                    ArrayList<Street> streetsWithTheNextStreet = new ArrayList<>(streets);
                    streetsWithTheNextStreet.add(proceedableJunction.getStreet());
                    newScore = score;
                    if (streetToIsZeroScore.get(proceedableJunction.getStreet()) == null)
                        newScore += proceedableJunction.getStreet().getRequiredTimeToFinishStreet();
                    Map<Street, Boolean> newStreetToWasTraveledAlready = new HashMap<>(streetToIsZeroScore);
                    newStreetToWasTraveledAlready.put(proceedableJunction.getStreet(), true);
                    if (!proceedableJunction.getStreet().isOneway())
                        newStreetToWasTraveledAlready.put(findInverseStreet(proceedableJunction.getStreet(), junctionToProceedableJunctions), true);
                    recursia(proceedableJunction.getJunctionId(),
                            timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet(), timeAllowed, newScore, bestPath,
                            streetsWithTheNextStreet, new HashMap<>(traveledAlready), newStreetToWasTraveledAlready,
                            junctionToProceedableJunctions);
                }
            }
        }
        if (isTimeExceeded) {
            if (score > bestPath.getScore()) {
                bestPath.setScore(score);
                bestPath.setStreets(streets);
                bestPath.setTime(timePassed);
            }
        }
    }

    private boolean canPassBestPath(double score, double timePassed, double timeAllowed, PathDetails bestPath) {
        return score + (timeAllowed - timePassed) > bestPath.getScore();
    }
}
