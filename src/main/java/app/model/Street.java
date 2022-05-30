package app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Street {
    private long junctionFromId;
    private long junctionToId;
    private boolean isOneway;
    private double requiredTimeToFinishStreet;
}
