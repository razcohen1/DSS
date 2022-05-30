package app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Junction {
    private long junctionId;
    private double x;
    private double y;
}
