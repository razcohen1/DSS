package app.algorithm.services;

import app.model.Junction;
import app.model.Street;
import org.junit.Test;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static app.algorithm.services.InverseStreetFinderTest.createJunctionWithId;
import static app.algorithm.services.ProceedableStreetsCalculator.createJunctionToProceedableStreetsMap;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProceedableStreetsCalculatorTest {

    @Test
    public void createCurrentJunctionToProceedableStreetsMapTest() {
        long junctionId1 = 1111;
        long junctionId2 = 2222;
        long junctionId3 = 3333;
        List<Junction> junctionsList = asList(createJunctionWithId(junctionId1), createJunctionWithId(junctionId2), createJunctionWithId(junctionId3));
        Street firstToSecondStreet = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId2).isOneway(true).build();
        Street firstToThirdStreet = Street.builder().junctionFromId(junctionId1).junctionToId(junctionId3).build();
        Street thirdToFirstStreet = Street.builder().junctionFromId(junctionId3).junctionToId(junctionId1).build();
        List<Street> streetList = asList(firstToSecondStreet, firstToThirdStreet, thirdToFirstStreet);
        MultiValueMap<Long, Street> junctionToProceedableStreetsMap = createJunctionToProceedableStreetsMap(junctionsList, streetList);
        assertThat(junctionToProceedableStreetsMap.get(junctionId1).size(), is(2));
        assertTrue(junctionToProceedableStreetsMap.get(junctionId1).containsAll(asList(firstToSecondStreet,firstToThirdStreet)));
        assertThat(junctionToProceedableStreetsMap.get(junctionId2).size(), is(0));
        assertTrue(junctionToProceedableStreetsMap.get(junctionId3).contains(thirdToFirstStreet));
    }
}