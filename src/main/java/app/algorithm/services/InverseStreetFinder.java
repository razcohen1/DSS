package app.algorithm.services;

import app.exceptions.InverseStreetNotFoundException;
import app.model.ProceedableJunction;
import app.model.Street;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InverseStreetFinder {
    public static Map<Street, Street> createStreetToInverseStreetMap(List<Street> streetList, MultiValueMap<Long, Street> junctionToProceedableStreets) {
        Map<Street, Street> streetToInverseStreetMap = new HashMap<>();
        streetList.stream()
                .filter(street -> !street.isOneway())
                .forEach(street -> streetToInverseStreetMap.put(street, findInverseStreet(street, junctionToProceedableStreets)));

        return streetToInverseStreetMap;
    }

    private static Street findInverseStreet(Street street, MultiValueMap<Long, Street> junctionToProceedableStreets) {
        return junctionToProceedableStreets.get(street.getJunctionToId()).stream()
                .filter(proceedableStreet -> proceedableStreet.getJunctionToId() == street.getJunctionFromId())
                .findAny()
                .orElseThrow(InverseStreetNotFoundException::new);
    }
}
