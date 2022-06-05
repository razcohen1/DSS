package app.algorithm.services;


import app.model.Path;
import app.model.ProblemInput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static java.lang.System.currentTimeMillis;
import static java.util.Optional.empty;
import static java.util.Optional.of;

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
        boolean doneSearching = false;
        while (shouldContinueSearching(doneSearching)) {
            Optional<Street> street = generateRandomNeighbor(currentJunction, traveledAlready, junctionToProceedableStreets, zeroScoreStreets);
            if (street.isPresent() && canFinishStreetInTime(street.get(), currentPath.getTime(), getTimeAllowed(problemInput))) {
                proceedToStreet(street.get(), currentPath, zeroScoreStreets, traveledAlready, streetToInverseStreet);
                currentJunction = street.get().getJunctionToId();
            } else if (!currentPath.isEmpty()) {
                Street lastAddedStreet = currentPath.getLastAddedStreet();
                undoProceedToStreet(lastAddedStreet, currentPath, zeroScoreStreets, streetToInverseStreet);
                currentJunction = lastAddedStreet.getJunctionFromId();
            } else {
                doneSearching = true;
            }

            replaceBestIfBetter(currentPath, bestPath);
        }

        return bestPath;
    }

    private boolean shouldContinueSearching(boolean doneSearching) {
        return maximumRunningTimeNotExceeded() && !doneSearching;
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

    private Optional<Street> generateRandomNeighbor(long currentJunction, List<Street> traveledAlready, MultiValueMap<Long, Street> junctionToProceedableStreets,
                                                    List<Street> zeroScoreStreets) {
        List<Street> streetsNotTraveled = new ArrayList<>(junctionToProceedableStreets.get(currentJunction));
        streetsNotTraveled.removeAll(traveledAlready);
        List<Street> streetsWorthScore = new ArrayList<>(streetsNotTraveled);
        streetsWorthScore.removeAll(zeroScoreStreets);

        if (!streetsWorthScore.isEmpty()) {
            return of(chooseRandomElement(streetsWorthScore));
        } else if (!streetsNotTraveled.isEmpty()) {
            return of(chooseRandomElement(streetsNotTraveled));
        } else {
            return empty();
        }
    }

    private Street chooseRandomElement(List<Street> streets) {
        return streets.get(new Random().nextInt(streets.size()));
    }

    private boolean maximumRunningTimeNotExceeded() {
        return getTimePassedInSeconds() < maximumRunningTimeInSeconds;
    }

    private double getTimePassedInSeconds() {
        return (double) (currentTimeMillis() - startTimeInMillis) / 1000;
    }

}
