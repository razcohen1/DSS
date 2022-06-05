package app.algorithm.services;


import app.model.Path;
import app.model.ProblemInput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;
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
    private double maximumRunningTimeInSeconds;
    private long startTimeInMillis;

    public Path find(ProblemInput problemInput) {
        startTimeInMillis = currentTimeMillis();
        Map<Street, Street> streetToInverseStreet = problemInput.getStreetToInverseStreet();
        MultiValueMap<Long, Street> junctionToProceedableStreets = problemInput.getJunctionToProceedableStreets();
        Path currentPath = Path.builder().score(0).streets(new ArrayList<>()).time(0).build();
        Path bestPath = Path.builder().score(0).build();
        long currentJunction = getInitialJunctionId(problemInput);
        List<Street> zeroScoreStreets = problemInput.getZeroScoreStreets();
        List<Street> traveledAlready = new ArrayList<>();
        while (maximumRunningTimeNotExceeded()) {
            Street street = generateRandomNeighbor(currentJunction, traveledAlready, junctionToProceedableStreets, zeroScoreStreets);
            if (street != null && canFinishStreetInTime(street, currentPath.getTime(), getTimeAllowed(problemInput))) {
                proceedToStreet(street, currentPath, zeroScoreStreets, traveledAlready, streetToInverseStreet);
                currentJunction = street.getJunctionToId();
            } else {
                if (currentPath.getStreets().isEmpty())
                    break;
                Street lastAddedStreet = currentPath.getLastAddedStreet();
                undoProceedToStreet(lastAddedStreet, currentPath, zeroScoreStreets, streetToInverseStreet);
                currentJunction = lastAddedStreet.getJunctionFromId();
            }

            replaceBestIfBetter(currentPath, bestPath);
        }

        return bestPath;
    }

    private double getTimeAllowed(ProblemInput problemInput) {
        return problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
    }

    private void undoProceedToStreet(Street lastAddedStreet, Path currentPath, List<Street> zeroScoreStreets, Map<Street, Street> streetToInverseStreet) {
        removeLastStreetFromRelevantLists(lastAddedStreet, currentPath, zeroScoreStreets);
        currentPath.decreaseScoreBy(calculateStreetScore(lastAddedStreet, zeroScoreStreets, streetToInverseStreet));
        currentPath.decreaseTimePassedBy(lastAddedStreet.getRequiredTimeToFinishStreet());
    }

    private void proceedToStreet(Street street, Path currentPath, List<Street> zeroScoreStreets, List<Street> traveledAlready, Map<Street, Street> streetToInverseStreet) {
        currentPath.increaseScoreBy(calculateStreetScore(street, zeroScoreStreets, streetToInverseStreet));
        addStreetToRelevantLists(street, currentPath, zeroScoreStreets, traveledAlready, streetToInverseStreet);
        currentPath.increaseTimePassedBy(street.getRequiredTimeToFinishStreet());
    }

    private boolean canFinishStreetInTime(Street street, double timePassed, double timeAllowed) {
        return timePassed + street.getRequiredTimeToFinishStreet() <= timeAllowed;
    }

    private long getInitialJunctionId(ProblemInput problemInput) {
        return problemInput.getMissionProperties().getInitialJunctionId();
    }

    private void replaceBestIfBetter(Path currentPath, Path bestPath) {
        if (currentPath.getScore() > bestPath.getScore()) {
            bestPath.setScore(currentPath.getScore());
            bestPath.setStreets(new ArrayList<>(currentPath.getStreets()));
            bestPath.setTime(currentPath.getTime());
        }
    }

    private double calculateStreetScore(Street street, List<Street> zeroScoreStreets, Map<Street, Street> streetToInverseStreet) {
        if (streetWorthScore(street, zeroScoreStreets, streetToInverseStreet))
            return street.getRequiredTimeToFinishStreet();

        return 0;
    }

    private boolean streetWorthScore(Street randomStreet, List<Street> zeroScoreStreets, Map<Street, Street> streetToInverseStreet) {
        return !zeroScoreStreets.contains(randomStreet) && !zeroScoreStreets.contains(streetToInverseStreet.get(randomStreet));
    }

    private Street getLastAddedStreet(List<Street> currentStreets) {
        return currentStreets.get(currentStreets.size() - 1);
    }

    private void removeLastStreetFromRelevantLists(Street lastAddedStreet, Path currentPath, List<Street> zeroScoreStreets) {
        currentPath.removeLastStreet();
        zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
        if (!lastAddedStreet.isOneway())
            zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
    }

    private void addStreetToRelevantLists(Street street, Path currentPath, List<Street> zeroScoreStreets, List<Street> traveledAlready, Map<Street, Street> streetToInverseStreet) {
        currentPath.addStreet(street);
        traveledAlready.add(street);
        zeroScoreStreets.add(street);
        if (!street.isOneway())
            zeroScoreStreets.add(streetToInverseStreet.get(street));
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

    private boolean maximumRunningTimeNotExceeded() {
        return getTimePassedInSeconds() < maximumRunningTimeInSeconds;
    }

    private double getTimePassedInSeconds() {
        return (double) (currentTimeMillis() - startTimeInMillis) / 1000;
    }

}
