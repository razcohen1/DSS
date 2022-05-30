package app.algorithm;


import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static app.algorithm.InverseStreetFinder.findInverseStreet;

public class BestPathFinder {
    public PathDetails findBestPath(ProblemInput problemInput, List<Street> zeroScoreStreets) {
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = problemInput.getJunctionToProceedableJunctions();
        long initialJunctionId = problemInput.getMissionProperties().getInitialJunctionId();
        double timeAllowedForCarsItinerariesInSeconds = problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
        PathDetails bestPath = PathDetails.builder().score(0).build();
        recursia(initialJunctionId, 0, timeAllowedForCarsItinerariesInSeconds, 0, bestPath,
                new ArrayList<>(), new ArrayList<>(), zeroScoreStreets, junctionToProceedableJunctions);
        return bestPath;
    }

    //todo: allow a limited amount of travels in the same street instead of only 1
    //todo: in dfs instead of contains use a boolean array or a map
    //todo: make sure im covering all the possible paths
    private void recursia(long currentJunction, double timePassed, double timeAllowed, double score, PathDetails bestPath,
                          List<Street> streets, List<Street> traveledAlready, List<Street> zeroScoreStreets,
                          MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions) {
        boolean isTimeExceeded = false;
        for (ProceedableJunction proceedableJunction : junctionToProceedableJunctions.get(currentJunction)) {
            if (!traveledAlready.contains(proceedableJunction.getStreet())) {
                if (timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet() > timeAllowed) {
                    isTimeExceeded = true;
                } else {
                    traveledAlready.add(proceedableJunction.getStreet());
//                    if(!proceedableJunction.getStreet().isOneway())
//                        traveledAlready.add(findInverseStreet(proceedableJunction.getStreet(),junctionToProceedableJunctions));
                    ArrayList<Street> streetsWithTheNextStreet = new ArrayList<>(streets);
                    streetsWithTheNextStreet.add(proceedableJunction.getStreet());
                    if (!zeroScoreStreets.contains(proceedableJunction.getStreet()))
                        score += proceedableJunction.getStreet().getRequiredTimeToFinishStreet();
                    List<Street> zeroScoreStreetsAfterStreet = new ArrayList<>(zeroScoreStreets);
                    zeroScoreStreetsAfterStreet.add(proceedableJunction.getStreet());
                    zeroScoreStreetsAfterStreet.add(findInverseStreet(proceedableJunction.getStreet(), junctionToProceedableJunctions));
                    recursia(proceedableJunction.getJunctionId(),
                            timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet(), timeAllowed, score, bestPath,
                            streetsWithTheNextStreet, new ArrayList<>(traveledAlready), zeroScoreStreetsAfterStreet,
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
}
