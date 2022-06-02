package app.algorithm.services;

import app.model.Junction;
import app.model.ProblemInput;
import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class JunctionToProceedableJunctionsCreator {
    public static MultiValueMap<Long, ProceedableJunction> createJunctionToProceedableJunctionsMap(List<Junction> junctionList, List<Street> streetList) {
        MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions = new LinkedMultiValueMap<>();
        junctionList.forEach(junction -> junctionToProceedableJunctions.put(junction.getJunctionId(), new ArrayList<>()));
        streetList.forEach(street -> {
            junctionToProceedableJunctions.add(street.getJunctionFromId(),
                    ProceedableJunction.builder()
                            .junctionId(street.getJunctionToId())
                            .street(street)
//                                .requiredTimeToFinishStreet(street.getRequiredTimeToFinishStreet())
                            .build());
        });
        return junctionToProceedableJunctions;
    }
}
