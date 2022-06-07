package app.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionProperties {
    private int amountOfCars;
    private long initialJunctionId;
    private double timeAllowedForCarsItinerariesInSeconds;
}
