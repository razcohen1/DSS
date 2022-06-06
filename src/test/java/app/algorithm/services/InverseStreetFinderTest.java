package app.algorithm.services;

import app.model.Junction;
import app.model.Street;
import org.junit.Test;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static app.algorithm.services.InverseStreetFinder.createStreetToInverseStreetMap;
import static app.algorithm.services.ProceedableStreetsCalculator.createJunctionToProceedableStreetsMap;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class InverseStreetFinderTest {

    @Test
    public void createCorrectStreetToInverseStreetMapTest() {
        long junctionId1 = 1111;
        long junctionId2 = 2222;
        long junctionId3 = 3333;
        List<Junction> junctions = Arrays.asList(createJunctionWithId(junctionId1),
                createJunctionWithId(junctionId2),
                createJunctionWithId(junctionId1));
        Street firstToSecondStreet = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId2).build();
        Street secondToFirstStreet = Street.builder().junctionFromId(junctionId2).junctionToId(junctionId1).build();
        Street thirdToFirstStreet = Street.builder().junctionFromId(junctionId3).junctionToId(junctionId1).build();
        Street firstToThirdStreet = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId3).build();
        Street someOneWayStreet = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId1).isOneway(true).build();
        List<Street> streets = new ArrayList<Street>() {{
            add(firstToSecondStreet);
            add(someOneWayStreet);
            add(secondToFirstStreet);
            add(thirdToFirstStreet);
            add(firstToThirdStreet);
        }};
        MultiValueMap<Long, Street> junctionToProceedableStreetsMap = createJunctionToProceedableStreetsMap(junctions, streets);
        Map<Street, Street> streetToInverseStreetMap = createStreetToInverseStreetMap(streets, junctionToProceedableStreetsMap);
        assertThat(streetToInverseStreetMap.size(),is(4));
        assertThat(streetToInverseStreetMap.get(firstToThirdStreet),is(thirdToFirstStreet));
        assertThat(streetToInverseStreetMap.get(thirdToFirstStreet),is(firstToThirdStreet));
        assertThat(streetToInverseStreetMap.get(firstToSecondStreet),is(secondToFirstStreet));
        assertThat(streetToInverseStreetMap.get(secondToFirstStreet),is(firstToSecondStreet));
    }

    public static Junction createJunctionWithId(long junctionId) {
        return Junction.builder().junctionId(junctionId).build();
    }
}