package app.algorithm.services;

import app.model.Path;
import app.model.Street;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class StreetsScorer {

    public static Double calculateTotalScore(List<Path> bestPaths) {
        return bestPaths.stream().map(Path::getScore).reduce((double) 0, Double::sum);
    }

    public static List<Street> getZeroScoreStreetsFromPath(Path bestPath, Map<Street, Street> streetToInverseStreet) {
        List<Street> streets = ofNullable(bestPath.getStreets()).orElse(emptyList());
        List<Street> zeroScoreStreetsFromCurrentRun = new ArrayList<Street>(streets);
        streets.forEach(street -> {
            if (!street.isOneway()) {
                zeroScoreStreetsFromCurrentRun.add(streetToInverseStreet.get(street));
            }
        });

        return zeroScoreStreetsFromCurrentRun;
    }
}