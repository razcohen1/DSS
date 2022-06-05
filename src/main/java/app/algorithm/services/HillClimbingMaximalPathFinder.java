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
        while (maximumRunningTimeNotExceeded()) {
            Street street = generateRandomNeighbor(currentJunction, traveledAlready, junctionToProceedableStreets, zeroScoreStreets);
            if (street != null && (timePassed + street.getRequiredTimeToFinishStreet() <= timeAllowed)) {
                score += calculateStreetScore(street, zeroScoreStreets, streetToInverseStreet);
                proceedToStreet(street, currentStreets, zeroScoreStreets, traveledAlready, streetToInverseStreet);
                timePassed += street.getRequiredTimeToFinishStreet();
                currentJunction = street.getJunctionToId();
            } else {
                if (currentStreets.isEmpty())
                    break;
                Street lastAddedStreet = getLastAddedStreet(currentStreets);
                undoProceedToStreet(lastAddedStreet, zeroScoreStreets, currentStreets);
                score -= calculateStreetScore(lastAddedStreet, zeroScoreStreets, streetToInverseStreet);
                timePassed -= lastAddedStreet.getRequiredTimeToFinishStreet();
                currentJunction = lastAddedStreet.getJunctionFromId();
            }
            if (score > bestScore) {
                bestScore = score;
                bestStreets = new ArrayList<>(currentStreets);
                bestTimePassed = timePassed;
            }
        }

        return Path.builder().score(bestScore).streets(bestStreets).time(bestTimePassed).build();
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

    private void undoProceedToStreet(Street lastAddedStreet, List<Street> zeroScoreStreets, List<Street> currentStreets) {
        currentStreets.remove(currentStreets.size() - 1);
        zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
        if (!lastAddedStreet.isOneway())
            zeroScoreStreets.remove(zeroScoreStreets.size() - 1);
    }

    private void proceedToStreet(Street street, List<Street> currentStreets, List<Street> zeroScoreStreets, List<Street> traveledAlready, Map<Street, Street> streetToInverseStreet) {
        currentStreets.add(street);
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
