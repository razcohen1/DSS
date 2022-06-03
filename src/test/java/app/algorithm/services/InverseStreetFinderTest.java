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
import static app.algorithm.services.JunctionToProceedableStreetsCreator.createJunctionToProceedableStreetsMap;

public class InverseStreetFinderTest {

//    @Test
//    public void findInverseStreetTest() {
//        long junctionId1 = 1111;
//        long junctionId2 = 2222;
//        long someJunctionId = 3333;
//        List<Junction> junctions = Arrays.asList(createJunction(junctionId1),
//                createJunction(junctionId2),
//                createJunction(someJunctionId));
//        Street firstToSecondStreet = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId2).build();
//        Street secondToFirstStreet = Street.builder().junctionFromId(junctionId2).junctionToId(junctionId1).build();
//        List<Street> streets = new ArrayList<Street>() {{
//            add(firstToSecondStreet);
//            add(Street.builder().junctionFromId(junctionId1).junctionToId(someJunctionId).build());
//            add(secondToFirstStreet);
//            add(Street.builder().junctionFromId(junctionId2).junctionToId(someJunctionId).build());
//        }};
//        MultiValueMap<Long, ProceedableStreet> junctionToProceedableStreetsMap = JunctionToProceedableStreetsCreator.createJunctionToProceedableStreetsMap(junctions, streets);
//        assertThat(findInverseStreet(firstToSecondStreet,junctionToProceedableStreetsMap),is(secondToFirstStreet));
//    }

    @Test
    public void createStreetToInverseStreetMapTest() {
        long junctionId1 = 1111;
        long junctionId2 = 2222;
        long someJunctionId = 3333;
        List<Junction> junctions = Arrays.asList(createJunction(junctionId1),
                createJunction(junctionId2),
                createJunction(someJunctionId));
        Street firstToSecondStreet = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId2).build();
        Street secondToFirstStreet = Street.builder().junctionFromId(junctionId2).junctionToId(junctionId1).build();
        List<Street> streets = new ArrayList<Street>() {{
            add(firstToSecondStreet);
            add(Street.builder().junctionFromId(junctionId1).junctionToId(someJunctionId).isOneway(true).build());
            add(secondToFirstStreet);
            add(Street.builder().junctionFromId(junctionId2).junctionToId(someJunctionId).build());
            add(Street.builder().junctionFromId(someJunctionId).junctionToId(junctionId2).build());
        }};
        MultiValueMap<Long, Street> junctionToProceedableStreetsMap = createJunctionToProceedableStreetsMap(junctions, streets);
        Map<Street, Street> streetToInverseStreetMap = createStreetToInverseStreetMap(streets, junctionToProceedableStreetsMap);
        int i = 5;
    }

    private Junction createJunction(long junctionId) {
        return Junction.builder().junctionId(junctionId).build();
    }
}