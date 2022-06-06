package app.algorithm.services;

import app.model.Junction;
import app.model.Street;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class ProceedableStreetsCalculator {
    public static MultiValueMap<Long, Street> createJunctionToProceedableStreetsMap(List<Junction> junctionList, List<Street> streetList) {
        MultiValueMap<Long, Street> junctionToProceedableStreets = new LinkedMultiValueMap<>();
        junctionList.forEach(junction -> junctionToProceedableStreets.put(junction.getJunctionId(), new ArrayList<>()));
        streetList.forEach(street -> junctionToProceedableStreets.add(street.getJunctionFromId(), street));

        return junctionToProceedableStreets;
    }
}
