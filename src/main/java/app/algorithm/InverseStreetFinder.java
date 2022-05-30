package app.algorithm;

import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.MultiValueMap;

public class InverseStreetFinder {
    public static Street findInverseStreet(Street street, MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions) {
        return junctionToProceedableJunctions.get(street.getJunctionToId()).stream()
                .filter(proceedableJunction -> proceedableJunction.getJunctionId()==street.getJunctionFromId())
                .findAny()
                .orElseThrow(RuntimeException::new)
                .getStreet();
    }
}
