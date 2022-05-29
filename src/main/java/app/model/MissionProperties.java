package app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionProperties {
    private int amountOfCars;
    private long initialJunctionId;
    private double timeAllowedForCarsItinerariesInSeconds;
}
