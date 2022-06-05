package app.algorithm.services;


import app.model.Path;
import app.model.ProblemInput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.System.currentTimeMillis;

@Getter
@Setter
@Service
public class HillClimbingMaximalPathFinder {
    @Value(value = "${drop.early.paths.that.cant.beat.best.score.by:0}")
    private double dropEarlyPathsThatCantBeatBestScoreBy;
    private double probabilityToReplaceBest;
    private double maximumRunningTimeInSeconds;
    private long startTimeInMillis;
    private double coolingRate = 0.9995;

    //TODO: whats going on with timepassed
    //TODO: should we go to else when time passed time allowed?
    public Path find(ProblemInput problemInput) {
        startTimeInMillis = currentTimeMillis();
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        MultiValueMap<Long, Street> junctionToProceedableStreets = problemInput.getJunctionToProceedableStreets();
        Path bestPath = Path.builder().score(0).build();
//        find(getInitialJunctionId(problemInput), 0, getTimeAllowed(problemInput), 0, bestPath,
//                new ArrayList<>(), new ArrayList<>(), problemInput.getZeroScoreStreets(), junctionToProceedableStreets, streetToInverseStreet);
        double timePassed = 0;
        double bestTimePassed = 0;
        double timeAllowed = problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
        double score = 0;
        double bestScore = 0;
        List<Street> bestStreets = new ArrayList<>();
        List<Street> currentStreets = new ArrayList<>();
        long currentJunction = problemInput.getMissionProperties().getInitialJunctionId();
        List<Street> zeroScoreStreets = problemInput.getZeroScoreStreets();
        List<Street> traveledAlready = new ArrayList<>();
        double t = 10;
        while (maximumRunningTimeNotExceeded()) {
            Street randomStreet = generateRandomNeighbor(currentJunction, traveledAlready, junctionToProceedableStreets, zeroScoreStreets);
            if (randomStreet != null && (timePassed + randomStreet.getRequiredTimeToFinishStreet() <= timeAllowed
//score is better with that line    && !zeroScoreStreets.contains(randomStreet) && !zeroScoreStreets.contains(streetToInverseStreet.get(randomStreet))
//                    || maybeAccept(score, score, t)
            )) {
                if (!zeroScoreStreets.contains(randomStreet) && !zeroScoreStreets.contains(streetToInverseStreet.get(randomStreet)))
                    score += randomStreet.getRequiredTimeToFinishStreet();
                currentStreets.add(randomStreet);
                traveledAlready.add(randomStreet);
                zeroScoreStreets.add(randomStreet);
                if (!randomStreet.isOneway())
                    zeroScoreStreets.add(streetToInverseStreet.get(randomStreet));
                timePassed += randomStreet.getRequiredTimeToFinishStreet();
                currentJunction = randomStreet.getJunctionToId();
            } else {
                if (score > bestScore) {
                    bestScore = score;
                    bestStreets = new ArrayList<>(currentStreets);
                    bestTimePassed = timePassed;
                }
                if (currentStreets.isEmpty())
                    break;
                Street lastAddedStreet = currentStreets.get(currentStreets.size() - 1);
                currentStreets.remove(currentStreets.size() - 1);
                zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
                if (!lastAddedStreet.isOneway())
                    zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
                if (!zeroScoreStreets.contains(lastAddedStreet) && !zeroScoreStreets.contains(streetToInverseStreet.get(lastAddedStreet)))
                    score -= lastAddedStreet.getRequiredTimeToFinishStreet();
                timePassed -= lastAddedStreet.getRequiredTimeToFinishStreet();
                currentJunction = lastAddedStreet.getJunctionFromId();
            }
            if (score > bestScore) {
                bestScore = score;
                bestStreets = new ArrayList<>(currentStreets);
                bestTimePassed = timePassed;
            }

            t *= coolingRate;
        }

        return Path.builder()
                .score(bestScore)
                .streets(bestStreets)
                .time(bestTimePassed)
                .build();
    }

    private boolean maybeAccept(double score, double newScore, double t) {
        return Math.exp((score - newScore) / t) < Math.random();
    }

    private Street generateRandomNeighbor(long currentJunction, List<Street> traveledAlready, MultiValueMap<Long, Street> junctionToProceedableStreets,
                                          List<Street> zeroScoreStreets) {
        List<Street> proceedableStreetsNotTraveledAlready = new ArrayList<>(junctionToProceedableStreets.get(currentJunction));
        proceedableStreetsNotTraveledAlready.removeAll(traveledAlready);
        List<Street> proceedableStreetsWithoutTraveledAndZero = new ArrayList<>(proceedableStreetsNotTraveledAlready);
        proceedableStreetsWithoutTraveledAndZero.removeAll(zeroScoreStreets);
        if (!proceedableStreetsWithoutTraveledAndZero.isEmpty()) {
            return proceedableStreetsWithoutTraveledAndZero.get(new Random().nextInt(proceedableStreetsWithoutTraveledAndZero.size()));
        } else if (!proceedableStreetsNotTraveledAlready.isEmpty()) {
            return proceedableStreetsNotTraveledAlready.get(new Random().nextInt(proceedableStreetsNotTraveledAlready.size()));
        } else {
            return null;
        }
    }

    private void find(long currentJunction, double timePassed, double timeAllowed, double score, Path bestPath,
                      List<Street> currentPath, List<Street> alreadyTraveledStreets, List<Street> zeroScoreStreets,
                      MultiValueMap<Long, Street> junctionToProceedableStreets, Map<Street, Street> streetToInverseStreet) {
        double newScore;
        if (maximumRunningTimeNotExceeded())
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
        if (score > bestPath.getScore() || Math.random() < probabilityToReplaceBest) {
            bestPath.setScore(score);
            bestPath.setStreets(new ArrayList<>(currentPath));
            bestPath.setTime(timePassed);
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
