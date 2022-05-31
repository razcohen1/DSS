package app.algorithm;

import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InverseStreetFinder {
    public static Map<Street,Street> createStreetToInverseStreetMap(List<Street> streetList, MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions) {
        Map<Street,Street> streetToInverseStreetMap = new HashMap<>();
        streetList.stream()
                .filter(street -> !street.isOneway())
                .forEach(street -> streetToInverseStreetMap.put(street,findInverseStreet(street,junctionToProceedableJunctions)));
        return streetToInverseStreetMap;
    }

    public static Street findInverseStreet(Street street, MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions) {
        try{
            return junctionToProceedableJunctions.get(street.getJunctionToId()).stream()
                    .filter(proceedableJunction -> proceedableJunction.getJunctionId() == street.getJunctionFromId())
                    .findAny()
                    .orElseThrow(RuntimeException::new)
                    .getStreet();
        }
        catch (Exception e){
            return null;
        }
    }
}
