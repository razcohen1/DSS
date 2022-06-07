package app.algorithm.services.neighbor.generator.implementation;

import app.algorithm.services.neighbor.generator.NeighborGenerator;
import app.model.Path;
import app.model.ProblemInput;
import app.model.Street;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Service
@ConditionalOnProperty(value = "algorithm.implementation", havingValue = "hillclimbing")
public class HillClimbingNeighborGenerator implements NeighborGenerator {
    @Override
    public Optional<Street> generate(long currentJunction, List<Street> traveledAlready, Path currentPath, ProblemInput problemInput) {
        List<Street> streetsNotTraveled = new ArrayList<>(problemInput.getJunctionToProceedableStreets().get(currentJunction));
        streetsNotTraveled.removeAll(traveledAlready);
        List<Street> streetsWorthScore = new ArrayList<>(streetsNotTraveled);
        streetsWorthScore.removeAll(problemInput.getZeroScoreStreets());

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
}
