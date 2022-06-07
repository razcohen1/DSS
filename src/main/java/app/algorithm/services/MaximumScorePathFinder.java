package app.algorithm.services;


import app.model.Path;
import app.model.ProblemInput;
import app.model.Street;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Service
public class MaximumScorePathFinder {
    @Value(value = "${drop.early.paths.that.cant.beat.best.score.by:0}")
    private double dropEarlyPathsThatCantBeatBestScoreBy;
    private double probabilityToReplaceBest;
    private double maximumRunningTimeInSeconds;
    private long startTimeInMillis;
    @Builder.Default
    private boolean checkLimitedNumberOfStreetsAhead = false;
    @Value(value = "${number.of.street.forward.to.check:20}")
    private int numberOfStreetsForward;

    public Path find(ProblemInput problemInput) {
        startTimeInMillis = currentTimeMillis();
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        MultiValueMap<Long, Street> junctionToProceedableStreets = problemInput.getJunctionToProceedableStreets();
        Path bestPath = Path.builder().streets(emptyList()).score(0).build();
        find(getInitialJunctionId(problemInput), 0, getTimeAllowed(problemInput), 0, bestPath,
                new ArrayList<>(), new ArrayList<>(), problemInput.getZeroScoreStreets(), junctionToProceedableStreets, streetToInverseStreet);

        return bestPath;
    }

    private void find(long currentJunction, double timePassed, double timeAllowed, double score, Path bestPath,
                      List<Street> currentPath, List<Street> alreadyTraveledStreets, List<Street> zeroScoreStreets,
                      MultiValueMap<Long, Street> junctionToProceedableStreets, Map<Street, Street> streetToInverseStreet) {
        double newScore;
        if (maximumRunningTimeNotExceeded() && numberOfStreetsAheadNotExceeded(currentPath))
            for (Street proceedableStreet : junctionToProceedableStreets.get(currentJunction)) {
                if (streetShouldBeTraveled(proceedableStreet, score, timePassed, timeAllowed, bestPath, alreadyTraveledStreets)) {
                    newScore = calculateNewScore(score, zeroScoreStreets, proceedableStreet);
                    addStreetToRelevantLists(proceedableStreet, alreadyTraveledStreets, zeroScoreStreets, currentPath, streetToInverseStreet);
                    find(proceedableStreet.getJunctionToId(), timePassed + proceedableStreet.getRequiredTimeToFinishStreet(),
                            timeAllowed, newScore, bestPath, currentPath, alreadyTraveledStreets, zeroScoreStreets,
                            junctionToProceedableStreets, streetToInverseStreet);
                    removeLastAddedStreetFromRelevantLists(proceedableStreet, alreadyTraveledStreets, zeroScoreStreets, currentPath);
                }
            }
        replaceBestPathIfBetter(currentPath, score, bestPath, timePassed);
    }

    private boolean numberOfStreetsAheadNotExceeded(List<Street> currentPath) {
        return !checkLimitedNumberOfStreetsAhead || currentPath.size() < numberOfStreetsForward;
    }

    private boolean streetShouldBeTraveled(Street proceedableStreet, double score, double timePassed, double timeAllowed, Path bestPath, List<Street> alreadyTraveledStreets) {
        return canFinishStreetInTime(timePassed, timeAllowed, proceedableStreet) &&
                canPassBestScore(score, timePassed, timeAllowed, bestPath) &&
                streetHasNotBeenTraveledAlready(proceedableStreet, alreadyTraveledStreets);
    }

    private boolean streetHasNotBeenTraveledAlready(Street proceedableStreet, List<Street> traveledAlready) {
        return !traveledAlready.contains(proceedableStreet);
    }

    private void addStreetToRelevantLists(Street proceedableStreet, List<Street> traveledAlready, List<Street> zeroScoreStreets, List<Street> currentPath, Map<Street, Street> streetToInverseStreet) {
        traveledAlready.add(proceedableStreet);
        currentPath.add(proceedableStreet);
        zeroScoreStreets.add(proceedableStreet);
        if (!proceedableStreet.isOneway())
            zeroScoreStreets.add(streetToInverseStreet.get(proceedableStreet));
    }

    private void removeLastAddedStreetFromRelevantLists(Street proceedableStreet, List<Street> traveledAlready, List<Street> zeroScoreStreets, List<Street> currentPath) {
        traveledAlready.remove(traveledAlready.size() - 1);
        currentPath.remove(currentPath.size() - 1);
        zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
        if (!proceedableStreet.isOneway())
            zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
    }

    private void replaceBestPathIfBetter(List<Street> currentPath, double score, Path bestPath, double timePassed) {
        if (score > bestPath.getScore() && Math.random() < probabilityToReplaceBest) {
            bestPath.setScore(score);
            bestPath.setStreets(new ArrayList<>(currentPath));
            bestPath.setTimePassed(timePassed);
        }
    }

    private double calculateNewScore(double score, List<Street> zeroScoreStreets, Street proceedableStreet) {
        double newScore = score;
        if (!zeroScoreStreets.contains(proceedableStreet))
            newScore += proceedableStreet.getRequiredTimeToFinishStreet();

        return newScore;
    }

    private boolean canFinishStreetInTime(double timePassed, double timeAllowed, Street proceedableStreet) {
        return timePassed + proceedableStreet.getRequiredTimeToFinishStreet() <= timeAllowed;
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
        return (double) (currentTimeMillis() - startTimeInMillis) / 1000;
    }

    private boolean canPassBestScore(double score, double timePassed, double timeAllowed, Path bestPath) {
        return score + (timeAllowed - timePassed) > bestPath.getScore() + dropEarlyPathsThatCantBeatBestScoreBy;
    }
}
