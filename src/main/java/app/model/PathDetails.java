package app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PathDetails {
    List<Long> junctions;
    double time;
}
