package app.algorithm.services;


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

import static java.lang.System.currentTimeMillis;

@Getter
@Setter
public class BestPathFinder {
    private final double percentOptimizationOverPerformance = 100;
    private double probabilityToReplaceBest = 1;
    private double maximumRunningTimeInSeconds = 10;
    private long startTimeInMillis;

    public PathDetails findBestPath(ProblemInput problemInput) {
        startTimeInMillis = currentTimeMillis();
        List<Street> zeroScoreStreets = problemInput.getZeroScoreStreets();
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = problemInput.getJunctionToProceedableJunctions();
        long initialJunctionId = problemInput.getMissionProperties().getInitialJunctionId();
        double timeAllowedForCarsItinerariesInSeconds = problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
        PathDetails bestPath = PathDetails.builder().score(0).build();
        recursia(initialJunctionId, 0, timeAllowedForCarsItinerariesInSeconds, 0, bestPath,
                new ArrayList<>(), new ArrayList<>(), zeroScoreStreets, junctionToProceedableJunctions, streetToInverseStreet);
        return bestPath;
    }

    private void recursia(long currentJunction, double timePassed, double timeAllowed, double score, PathDetails bestPath,
                          List<Street> streets, List<Street> traveledAlready, List<Street> zeroScoreStreets,
                          MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions, Map<Street, Street> streetToInverseStreet) {
        double newScore;
        if (maximumRunningTimeNotExceeded())
            for (ProceedableJunction proceedableJunction : junctionToProceedableJunctions.get(currentJunction)) {
                if (!traveledAlready.contains(proceedableJunction.getStreet()) && canPassBestPath(score, timePassed, timeAllowed, bestPath)) {
                    if (timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet() <= timeAllowed) {
                        traveledAlready.add(proceedableJunction.getStreet());
//                    if(!proceedableJunction.getStreet().isOneway())
//                        traveledAlready.add(findInverseStreet(proceedableJunction.getStreet(),junctionToProceedableJunctions));
                        streets.add(proceedableJunction.getStreet());
                        newScore = score;
                        if (!zeroScoreStreets.contains(proceedableJunction.getStreet()))
                            newScore += proceedableJunction.getStreet().getRequiredTimeToFinishStreet();
                        zeroScoreStreets.add(proceedableJunction.getStreet());
                        if (!proceedableJunction.getStreet().isOneway())
                            zeroScoreStreets.add(streetToInverseStreet.get(proceedableJunction.getStreet()));
                        recursia(proceedableJunction.getJunctionId(),
                                timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet(), timeAllowed, newScore, bestPath,
                                streets, traveledAlready, zeroScoreStreets,
                                junctionToProceedableJunctions, streetToInverseStreet);

                        traveledAlready.remove(traveledAlready.size() - 1);
                        streets.remove(streets.size() - 1);
                        zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
                        if (!proceedableJunction.getStreet().isOneway())
                            zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
                    }
                }
            }

        if (score > bestPath.getScore() && Math.random() < probabilityToReplaceBest) {
            bestPath.setScore(score);
            bestPath.setStreets(new ArrayList<>(streets));
            bestPath.setTime(timePassed);
        }

    }

    private boolean maximumRunningTimeNotExceeded() {
        return getTimePassedInSeconds() < maximumRunningTimeInSeconds;
    }

    private double getTimePassedInSeconds() {
        return (double) (currentTimeMillis() - startTimeInMillis)/1000;
    }

    private boolean canPassBestPath(double score, double timePassed, double timeAllowed, PathDetails bestPath) {
        return score + (timeAllowed - timePassed) > bestPath.getScore() + 100 * (100 - percentOptimizationOverPerformance) / 100;
    }
}
