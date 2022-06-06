package app.algorithm.services;

import app.model.Junction;
import app.model.Path;
import app.model.Street;
import org.junit.Test;

import java.util.List;

import static app.algorithm.services.InverseStreetFinder.createStreetToInverseStreetMap;
import static app.algorithm.services.InverseStreetFinderTest.createJunctionWithId;
import static app.algorithm.services.ProceedableStreetsCalculator.createJunctionToProceedableStreetsMap;
import static app.algorithm.services.StreetsScorer.calculateTotalScore;
import static app.algorithm.services.StreetsScorer.getZeroScoreStreetsFromPath;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StreetsScorerTest {

    @Test
    public void calculateTotalScoreSumsPathsScoreCorrectlyTest() {
        List<Path> paths = asList(createPathOfScore(1), createPathOfScore(2), createPathOfScore(3));
        assertThat(calculateTotalScore(paths), is((double) 6));
    }

    @Test
    public void getZeroScoreStreetsFromPathTest() {
        int junctionId1 = 1;
        int junctionId2 = 2;
        int junctionId3 = 3;
        List<Junction> junctions = asList(createJunctionWithId(junctionId1), createJunctionWithId(junctionId2), createJunctionWithId(3));
        Street firstToSecond = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId2).isOneway(true).build();
        Street secondToThird = Street.builder().junctionFromId(junctionId2).junctionToId(junctionId3).isOneway(false).build();
        Street thirdToSecond = Street.builder().junctionFromId(junctionId3).junctionToId(junctionId2).isOneway(false).build();
        Street randomStreet = Street.builder().junctionFromId(5).junctionToId(6).isOneway(true).build();
        List<Street> allStreets = asList(firstToSecond, secondToThird, thirdToSecond, randomStreet);
        List<Street> pathStreets = asList(firstToSecond, thirdToSecond);
        Path path = Path.builder()
                .streets(pathStreets)
                .build();

        List<Street> expectedZeroScoreStreets = asList(firstToSecond, secondToThird, thirdToSecond);
        List<Street> actualZeroScoreStreetsFromPath = getZeroScoreStreetsFromPath(path, createStreetToInverseStreetMap(allStreets, createJunctionToProceedableStreetsMap(junctions, allStreets)));
        assertThat(actualZeroScoreStreetsFromPath.size(), is(expectedZeroScoreStreets.size()));
        assertTrue(actualZeroScoreStreetsFromPath.containsAll(expectedZeroScoreStreets));
    }

    private Path createPathOfScore(double score) {
        return Path.builder().score(score).build();
    }
}