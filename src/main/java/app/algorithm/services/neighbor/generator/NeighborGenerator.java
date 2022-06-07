package app.algorithm.services.neighbor.generator;

import app.model.Path;
import app.model.ProblemInput;
import app.model.Street;

import java.util.List;
import java.util.Optional;

public interface NeighborGenerator {
    Optional<Street> generate(long currentJunction, List<Street> traveledAlready, Path currentPath, ProblemInput problemInput);
}
