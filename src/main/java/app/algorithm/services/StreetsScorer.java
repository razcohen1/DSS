package app.algorithm.services;

import app.model.PathDetails;
import app.model.Street;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StreetsScorer {

    public static Double calculateTotalScore(List<PathDetails> bestPaths) {
        return bestPaths.stream().map(PathDetails::getScore).reduce((double) 0, Double::sum);
    }

    public static List<Street> getZeroScoreStreetsFromPath(PathDetails bestPath, Map<Street, Street> streetToInverseStreet) {
        List<Street> streets = bestPath.getStreets();
        List<Street> zeroScoreStreetsFromCurrentRun = new ArrayList<Street>(streets);
        streets.forEach(street -> {
            if (!street.isOneway()) {
                zeroScoreStreetsFromCurrentRun.add(streetToInverseStreet.get(street));
            }
        });

        return zeroScoreStreetsFromCurrentRun;
    }
}