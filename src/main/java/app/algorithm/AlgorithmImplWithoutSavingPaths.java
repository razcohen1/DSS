//package app.algorithm;
//
//import app.model.PathDetails;
//import app.model.ProblemInput;
//import app.model.ProblemOutput;
//import app.model.Street;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static app.algorithm.InverseStreetFinder.findInverseStreet;
//
//@Getter
//@Setter
//public class AlgorithmImplWithoutSavingPaths implements Algorithm {
//    private AllValidPathsCalculator allValidPathsCalculator = new AllValidPathsCalculator();
//    private BestPathFinder bestPathFinder = new BestPathFinder();
//
//    //TODO: the problem with the bidirectional streets where they show up twice in the json and therefore passing them
//    // in both directions gives points
//    @Override
//    public ProblemOutput run(ProblemInput problemInput) {
//        List<PathDetails> validPaths = allValidPathsCalculator.calculate(problemInput);
//        List<Street> traversedAlreadyStreets = new ArrayList<>();
//        List<List<Street>> allStreetPaths = validPaths.stream().map(PathDetails::getStreets).collect(Collectors.toList());
//        List<List<Street>> bestCarsPaths = new ArrayList<>();
//        for (int carIndex = 0; carIndex < problemInput.getMissionProperties().getAmountOfCars(); carIndex++) {
//            List<Street> bestStreetsPath = null;
//            double bestScore = 0;
//            for (List<Street> streets : allStreetPaths) {
//                double score = 0;
//                for (Street street : streets) {
//                    if (!traversedAlreadyStreets.contains(street)) {
//                        score += street.getRequiredTimeToFinishStreet();
//                    }
//                }
//                if (score > bestScore) {
//                    bestScore = score;
//                    bestStreetsPath = streets;
//                }
//            }
//            bestCarsPaths.add(bestStreetsPath);
//            traversedAlreadyStreets.addAll(bestStreetsPath);
//            bestStreetsPath.forEach(street -> {
//                if(!street.isOneway())
//                    traversedAlreadyStreets.add(findInverseStreet(street,problemInput.getJunctionToProceedableJunctions()));
//            });
//        }
//        return ProblemOutput.builder().bestCarsPaths(bestCarsPaths).build();
//    }
//}
