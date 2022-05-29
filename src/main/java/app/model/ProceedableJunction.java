package app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProceedableJunction {
    private long junctionId;
    private Street street;
}
