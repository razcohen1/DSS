package app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Street {
    private long junctionFromId;
    private long junctionToId;
    private boolean isOneway;
    private double requiredTimeToFinishStreet;
}
