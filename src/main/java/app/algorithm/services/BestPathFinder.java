package app.algorithm.services;


import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProceedableJunction;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

@Getter
@Setter
@Service
public class BestPathFinder {
    @Value(value = "${percent.optimization.over.performance:100}")
    private double percentOptimizationOverPerformance;
    private double probabilityToReplaceBest;
    private double maximumRunningTimeInSeconds;
    private long startTimeInMillis;

    public PathDetails findBestPath(ProblemInput problemInput) {
        startTimeInMillis = currentTimeMillis();
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = problemInput.getJunctionToProceedableJunctions();
        PathDetails bestPath = PathDetails.builder().score(0).build();
        findBestPath(getInitialJunctionId(problemInput), 0, getTimeAllowed(problemInput), 0, bestPath,
                new ArrayList<>(), new ArrayList<>(), problemInput.getZeroScoreStreets(), junctionToProceedableJunctions, streetToInverseStreet);

        return bestPath;
    }

    private void findBestPath(long currentJunction, double timePassed, double timeAllowed, double score, PathDetails bestPath,
                              List<Street> currentPath, List<Street> traveledAlready, List<Street> zeroScoreStreets,
                              MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions, Map<Street, Street> streetToInverseStreet) {
        double newScore;
        if (maximumRunningTimeNotExceeded())
            for (ProceedableJunction proceedableJunction : junctionToProceedableJunctions.get(currentJunction)) {
                Street street = proceedableJunction.getStreet();
                if (!traveledAlready.contains(street) && canPassBestPath(score, timePassed, timeAllowed, bestPath)) {
                    if (timePassed + street.getRequiredTimeToFinishStreet() <= timeAllowed) {
                        traveledAlready.add(street);
//                    if(!proceedableJunction.getStreet().isOneway())
//                        traveledAlready.add(findInverseStreet(proceedableJunction.getStreet(),junctionToProceedableJunctions));
                        currentPath.add(street);
                        newScore = score;
                        if (!zeroScoreStreets.contains(street))
                            newScore += street.getRequiredTimeToFinishStreet();
                        zeroScoreStreets.add(street);
                        if (!street.isOneway())
                            zeroScoreStreets.add(streetToInverseStreet.get(street));
                        findBestPath(proceedableJunction.getJunctionId(),
                                timePassed + street.getRequiredTimeToFinishStreet(), timeAllowed, newScore, bestPath,
                                currentPath, traveledAlready, zeroScoreStreets,
                                junctionToProceedableJunctions, streetToInverseStreet);

                        traveledAlready.remove(traveledAlready.size() - 1);
                        currentPath.remove(currentPath.size() - 1);
                        zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
                        if (!street.isOneway())
                            zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
                    }
                }
            }

        if (score > bestPath.getScore() && Math.random() < probabilityToReplaceBest) {
            bestPath.setScore(score);
            bestPath.setStreets(new ArrayList<>(currentPath));
            bestPath.setTime(timePassed);
        }

    }

    private double getTimeAllowed(ProblemInput problemInput) {
        return problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
    }

    private long getInitialJunctionId(ProblemInput problemInput) {
        return problemInput.getMissionProperties().getInitialJunctionId();
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
