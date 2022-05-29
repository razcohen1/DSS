package app.algorithm;


import app.model.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class AllValidPathsCalculator {
    public List<Path> calculate(ProblemInput problemInput) {
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = createJunctionToProceedableJunctionsMap(problemInput);
        long initialJunctionId = problemInput.getMissionProperties().getInitialJunctionId();
        double timeAllowedForCarsItinerariesInSeconds = problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
        ArrayList<PathDetails> allFinalPaths = new ArrayList<>();
        recursia(initialJunctionId, 0, timeAllowedForCarsItinerariesInSeconds,
                new ArrayList<>(), allFinalPaths, new ArrayList<>(), junctionToProceedableJunctions);
        return null;
    }

    //todo: traveled already on streets instead of junctions (dealt with)
    //todo: duplicate paths when multiple neighbors exceeding time limit (dealt with)
    //todo: allow a limited amount of travels in the same street instead of only 1
    //todo: save all streets in path to evaluate the performance
    private void recursia(long currentJunction, double timePassed, double timeAllowed, List<Long> path,
                          List<PathDetails> allFinalPaths, List<Street> traveledAlready, MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions) {
        path.add(currentJunction);
        boolean exceededTimeLimit = false;
        for (ProceedableJunction proceedableJunction : junctionToProceedableJunctions.get(currentJunction)) {
            if (!traveledAlready.contains(proceedableJunction.getStreet())) {
                if (timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet() > timeAllowed) {
                    exceededTimeLimit = true;
                } else {
                    traveledAlready.add(proceedableJunction.getStreet());
                    recursia(proceedableJunction.getJunctionId(),
                            timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet(), timeAllowed,
                            new ArrayList<>(path), allFinalPaths, traveledAlready, junctionToProceedableJunctions);
                }
            }
        }
        if(exceededTimeLimit)
            allFinalPaths.add(PathDetails.builder().junctions(path).time(timePassed).build());
    }

    private MultiValueMap<Long, ProceedableJunction> createJunctionToProceedableJunctionsMap(ProblemInput problemInput) {
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = new LinkedMultiValueMap<>();
        problemInput.getJunctionsList().forEach(junction -> junctionToProceedableJunctions.put(junction.getJunctionId(), new ArrayList<>()));
        problemInput.getStreetsList().forEach(street -> {
            junctionToProceedableJunctions.add(street.getJunctionFromId(),
                    ProceedableJunction.builder()
                            .junctionId(street.getJunctionToId())
                            .street(street)
//                                .requiredTimeToFinishStreet(street.getRequiredTimeToFinishStreet())
                            .build());
        });
        return junctionToProceedableJunctions;
    }
}
