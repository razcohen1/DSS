package app.algorithm.services;

import app.model.Junction;
import app.model.ProblemInput;
import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class JunctionToProceedableStreetsCreator {
    public static MultiValueMap<Long, Street> createJunctionToProceedableStreetsMap(List<Junction> junctionList, List<Street> streetList) {
        MultiValueMap<Long, Street> junctionToProceedableJunctions = new LinkedMultiValueMap<>();
        junctionList.forEach(junction -> junctionToProceedableJunctions.put(junction.getJunctionId(), new ArrayList<>()));
        streetList.forEach(street -> junctionToProceedableJunctions.add(street.getJunctionFromId(), street));

        return junctionToProceedableJunctions;
    }
}
