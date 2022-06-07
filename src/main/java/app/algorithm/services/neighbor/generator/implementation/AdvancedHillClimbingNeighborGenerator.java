package app.algorithm.services.neighbor.generator.implementation;

import app.algorithm.services.MaximumScorePathFinder;
import app.algorithm.services.neighbor.generator.NeighborGenerator;
import app.model.MissionProperties;
import app.model.Path;
import app.model.ProblemInput;
import app.model.Street;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Service
@ConditionalOnProperty(value = "algorithm.implementation", havingValue = "advancedhillclimbing")
public class AdvancedHillClimbingNeighborGenerator implements NeighborGenerator {
    @Autowired
    private MaximumScorePathFinder maximumScorePathForwardFinder;

    @PostConstruct
    public void postConstruct() {
        maximumScorePathForwardFinder.setProbabilityToReplaceBest(1);
        maximumScorePathForwardFinder.setCheckLimitedNumberOfStreetsForward(true);
        maximumScorePathForwardFinder.setMaximumRunningTimeInSeconds(Integer.MAX_VALUE);
    }

    @Override
    public Optional<Street> generate(long currentJunction, List<Street> traveledAlready, Path currentPath, ProblemInput problemInput) {
        Path path = maximumScorePathForwardFinder.find(createInputForCheckingBestPathForward(currentJunction, currentPath, problemInput));
        if (!firstStreetFromPathCanBeTaken(traveledAlready, path))
            return empty();

        return of(path.getFirstStreet());
    }

    private boolean firstStreetFromPathCanBeTaken(List<Street> traveledAlready, Path path) {
        return !path.isEmpty() && !traveledAlready.contains(path.getFirstStreet());
    }

    private ProblemInput createInputForCheckingBestPathForward(long currentJunction, Path currentPath, ProblemInput problemInput) {
        return ProblemInput.builder()
                .streetsList(problemInput.getStreetsList())
                .junctionsList(problemInput.getJunctionsList())
                .junctionToProceedableStreets(problemInput.getJunctionToProceedableStreets())
                .streetToInverseStreet(problemInput.getStreetToInverseStreet())
                .missionProperties(MissionProperties.builder()
                        .amountOfCars(problemInput.getMissionProperties().getAmountOfCars())
                        .timeAllowedForCarsItinerariesInSeconds(calculateTimeRemain(currentPath, problemInput))
                        .initialJunctionId(currentJunction)
                        .build())
                .zeroScoreStreets(problemInput.getZeroScoreStreets())
                .build();
    }

    private double calculateTimeRemain(Path currentPath, ProblemInput problemInput) {
        return problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds() - currentPath.getTimePassed();
    }
}
