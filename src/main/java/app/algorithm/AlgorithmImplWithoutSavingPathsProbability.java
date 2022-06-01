package app.algorithm;

import app.model.PathDetails;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AlgorithmImplWithoutSavingPathsProbability implements Algorithm {
    private BestPathFinder bestPathFinder = new BestPathFinder();
    private int numberOfTimesToRunTheAlgorithm = 100;

    //TODO: the problem with the bidirectional streets where they show up twice in the json and therefore passing them
    // in both directions gives points
    @Override
    public ProblemOutput run(ProblemInput problemInput) {
        List<ProblemOutput> problemOutputs = new ArrayList<>();
        ProblemOutput best = ProblemOutput.builder().totalScore(0).build();
        for (int i = 0; i < numberOfTimesToRunTheAlgorithm; i++) {
            ProblemOutput problemOutput = runOnce(problemInput);
            problemOutputs.add(problemOutput);
            if (problemOutput.getTotalScore() > best.getTotalScore())
                best = problemOutput;
        }
        System.out.println("best = " + best.getTotalScore());
        return best;
    }

    private ProblemOutput runOnce(ProblemInput problemInput) {
        bestPathFinder.setProbabiltyToReplaceBest(0.2);
        List<PathDetails> bestPaths = new ArrayList<>();
        List<List<Street>> bestCarsPaths = new ArrayList<>();
        List<Street> traversedAlreadyStreets = new ArrayList<>();
        for (int carIndex = 0; carIndex < problemInput.getMissionProperties().getAmountOfCars(); carIndex++) {
            PathDetails bestPath = bestPathFinder.findBestPath(problemInput, traversedAlreadyStreets);
            bestCarsPaths.add(bestPath.getStreets());
            bestPaths.add(bestPath);
            traversedAlreadyStreets.addAll(bestPath.getStreets());
            bestPath.getStreets().forEach(street -> {
                if (!street.isOneway())
                    traversedAlreadyStreets.add(problemInput.getStreetToInverseStreet().get(street));
            });
            bestPathFinder.setProbabiltyToReplaceBest(bestPathFinder.getProbabiltyToReplaceBest() + 0.2);
        }
        double totalScore = bestPaths.stream().map(PathDetails::getScore).reduce((double) 0, Double::sum);
        System.out.println(totalScore);
        return ProblemOutput.builder().bestPaths(bestPaths).bestCarsPaths(bestCarsPaths).totalScore(totalScore).build();
    }
}
