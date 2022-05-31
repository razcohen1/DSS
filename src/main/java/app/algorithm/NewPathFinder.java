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
                new ArrayList<>(), new HashMap<>(), streetToIsZeroScore, junctionToProceedableJunctions, problemInput.getStreetToInverseStreet(), Street.builder().build());
        return bestPath;
    }

    //todo: allow a limited amount of travels in the same street instead of only 1
    //todo: in dfs instead of contains use a boolean array or a map
    //todo: make sure im covering all the possible paths
    //todo: change streets to LinkedList cause using add
    private void recursia(long currentJunction, double timePassed, double timeAllowed, double score, PathDetails bestPath,
                          List<Street> streets, Map<Street, Boolean> traveledAlready, Map<Street, Boolean> streetToIsZeroScore,
                          MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions, Map<Street, Street> streetToInverseStreet,
                          Street lastStreet) {
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
                    streets.add(proceedableJunction.getStreet());
                    newScore = score;
                    if (streetToIsZeroScore.get(proceedableJunction.getStreet()) == null)
                        newScore += proceedableJunction.getStreet().getRequiredTimeToFinishStreet();
                    streetToIsZeroScore.put(proceedableJunction.getStreet(), true);
                    if (!proceedableJunction.getStreet().isOneway())
                        streetToIsZeroScore.put(streetToInverseStreet.get(proceedableJunction.getStreet()), true);
                    recursia(proceedableJunction.getJunctionId(),
                            timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet(), timeAllowed, newScore, bestPath,
                            streets, traveledAlready, streetToIsZeroScore,
                            junctionToProceedableJunctions, streetToInverseStreet, proceedableJunction.getStreet());
                }
            }
        }
        if (isTimeExceeded) {
            if (score > bestPath.getScore()) {
                bestPath.setScore(score);
                bestPath.setStreets(new ArrayList<>(streets));
                bestPath.setTime(timePassed);
            }
        }
        streets.remove(lastStreet);
        traveledAlready.remove(lastStreet);
        streetToIsZeroScore.remove(lastStreet);
        streetToIsZeroScore.remove(streetToInverseStreet.get(lastStreet));
    }

    private boolean canPassBestPath(double score, double timePassed, double timeAllowed, PathDetails bestPath) {
        return score + (timeAllowed - timePassed) > bestPath.getScore();
    }
}
