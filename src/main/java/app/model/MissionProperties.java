package app.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

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
