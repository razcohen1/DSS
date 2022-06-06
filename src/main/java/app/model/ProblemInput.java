package app.model;

import lombok.*;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemInput {
    private MissionProperties missionProperties;
    private List<Junction> junctionsList;
    private List<Street> streetsList;
    private MultiValueMap<Long, Street> junctionToProceedableStreets;
    private Map<Street,Street> streetToInverseStreet;
    private List<Street> zeroScoreStreets;
}
