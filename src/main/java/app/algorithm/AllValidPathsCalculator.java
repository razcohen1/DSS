package app.algorithm;


import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class AllValidPathsCalculator {
    public List<PathDetails> calculate(ProblemInput problemInput) {
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = problemInput.getJunctionToProceedableJunctions();
        long initialJunctionId = problemInput.getMissionProperties().getInitialJunctionId();
        double timeAllowedForCarsItinerariesInSeconds = problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds();
        List<PathDetails> allFinalPaths = new ArrayList<>();
        recursia(initialJunctionId, 0, timeAllowedForCarsItinerariesInSeconds,
                new ArrayList<>(), new ArrayList<>(), allFinalPaths, new ArrayList<>(), junctionToProceedableJunctions);
        return allFinalPaths;
    }

    //todo: allow a limited amount of travels in the same street instead of only 1
    //todo: in dfs instead of contains use a boolean array or a map
    //todo: make sure im covering all the possible paths
    //todo: deal with the case where a path has no neighbor streets that it hasnt traveled through already (currently drops the path)
    private void recursia(long currentJunction, double timePassed, double timeAllowed, List<Long> path,
                          List<Street> streets, List<PathDetails> allFinalPaths, List<Street> traveledAlready,
                          MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions) {
        path.add(currentJunction);
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
                    recursia(proceedableJunction.getJunctionId(),
                            timePassed + proceedableJunction.getStreet().getRequiredTimeToFinishStreet(), timeAllowed,
                            new ArrayList<>(path), streetsWithTheNextStreet, allFinalPaths, new ArrayList<>(traveledAlready), junctionToProceedableJunctions);
                }
            }
        }
        if (isTimeExceeded)
            allFinalPaths.add(PathDetails.builder().junctions(path).streets(streets).time(timePassed).build());
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
